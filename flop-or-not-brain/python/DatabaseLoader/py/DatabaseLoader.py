import boto3
import json
import requests
import time
from decimal import Decimal

# RESULTS_PER_PAGE = 20
IGNORED_KEYS = ["also_known_as", "biography", "overview", "production_companies", "belongs_to_collection", "homepage", "spoken_languages"]
DECIMAL_KEYS = ["popularity", "vote_average"]

dynamodb = boto3.resource("dynamodb")
table = dynamodb.Table("flopOrNot")
batch = table.batch_writer()

OMDB_URL = "http://www.omdbapi.com/?apikey=85704252&i="
KEYS = ["Rated", "Ratings", "Metascore", "imdbRating", "imdbVotes", "BoxOffice", "Production"]

def getImdbData(imdbId):
    response = requests.get(OMDB_URL + str(imdbId))
    data = {}
    
    if response.status_code == 200:
        for key in KEYS:
            jsonData = json.loads(response.text)
            if (jsonData[key]):
                data[key] = jsonData[key]
    return data

def saveData(json, itemPrefix):
    """Remove ignored keys, process decimal keys, and remove empty keys then save the object to the database"""
    
    # remove ignored keys
    for key in IGNORED_KEYS:
        json.pop(key, None)
    
    # process decimal keys
    for key in DECIMAL_KEYS:
        if key in json:
            json[key] = Decimal(json[key]).quantize(Decimal('1.000'))
    
    # remove empty keys
    keysToPop = []
    for key in json:
        if json[key] == "":
            keysToPop.append(key)            
    for key in keysToPop:
        json.pop(key)
        
    # get data from imdb
    if itemPrefix == "movie":
        imdbData = getImdbData(json["imdb_id"])
    else:
        imdbData = {}

    try:
        # save the item to the database
        batch.put_item(
            Item={**json, **imdbData}
        )
    except:
        print("Error with:")
        print(json)


def getMovieDBUrl(suffix):
    """Get the URL for themoviedb API given the suffix"""
    return "https://api.themoviedb.org/3/" + suffix + "?api_key=670a2f04173250a43eca59d9e2c922d9"


def fetchAndSaveItem(itemId, itemPrefix):
    if itemPrefix == "person":
        url = getMovieDBUrl("person/" + str(itemId))
    else:
        url = getMovieDBUrl("movie/" + str(itemId))
    response = requests.get(url)
    
    if response.status_code == 200:
        jsonData = json.loads(response.text)
        itemId = itemPrefix + "-" + str(jsonData["id"])
        jsonData["itemId"] = itemId
        jsonData["relatedItemId"] = itemId
        
        saveData(jsonData, itemPrefix)
        
        print("Success: " + itemId)
    else:
        if response.status_code == 429:
            waitTime = response.headers["Retry-After"]
            print("429 waiting " + str(waitTime))
            time.sleep(float(waitTime))
        print("Error : " + itemPrefix + "-" + str(itemId) + " " + str(response.status_code))

    return response.status_code


def fetchAndSavePersonById(personId):
    fetchAndSaveItem(personId, "person")

    
def fetchAndSaveMovieById(movieId):
    fetchAndSaveItem(movieId, "movie")


def fetchAndSaveMovieCrewData(movieId):
    response = requests.get(getMovieDBUrl("movie/" + str(movieId) + "/credits"))
    
    if response.status_code == 200:
        jsonData = json.loads(response.text)
        
        # add each cast member as an actor
        if "cast" in jsonData:
            for castMember in jsonData["cast"]:
                saveData({
                    "itemId": "person-" + str(castMember["id"]),
                    "relatedItemId": "movie-" + str(movieId),
                    "job": "Actor",
                    "credit_id": str(castMember["credit_id"])
                })
        else:
            print("No cast data for " + str(movieId))
        
        # add the director
        if "crew" in jsonData:
            for crewMember in jsonData["crew"]:
                if crewMember["job"] == "Director":
                    saveData({
                        "itemId": "person-" + str(crewMember["id"]),
                        "relatedItemId": "movie-" + str(movieId),
                        "job": crewMember["job"],
                        "credit_id": str(crewMember["credit_id"])
                    })
        else:
            print("No crew data for " + str(movieId))                
    else:
        print("Error code: " + str(response.status_code))


def loopAndAdd(startRange):
    for i in range(startRange, startRange + 10):
        fetchAndSavePersonById(i)
        
        # fetch movie, if it's there, fetch the crew data
        if fetchAndSaveMovieById(i) == 200:
            fetchAndSaveMovieCrewData(i)
            
        #print("Completed fetching and loading on index " + str(i))
        
    loopAndAdd(startRange + 10)


def main():    
    loopAndAdd(1490)

        
main()

# def saveFetchedMovieData(iJsonData, iActorId):
#     for i in range (0, min(RESULTS_PER_PAGE, len(iJsonData["results"]))):
#         movieData = iJsonData["results"][i]
#         saveData({
#             "itemId": "person-" + str(iActorId),
#             "relatedItemId": "movie-" + str(movieData["id"]),
#             "role": "actor"
#         })
# 
#         
# def retrieveAndSaveMovieDataByPage(iActorId, iPageNum):
#     response = requests.get(getMovieDBUrl("discover/movie") + "&with_cast=" + str(iActorId) + "&page=" + str(iPageNum))
#     jsonData = json.loads(response.text)
#     saveFetchedMovieData(jsonData, iActorId)
#     return jsonData.get("total_results")
