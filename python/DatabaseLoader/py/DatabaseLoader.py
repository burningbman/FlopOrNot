import boto3
import json
import requests
from decimal import Decimal

# RESULTS_PER_PAGE = 20
IGNORED_KEYS = ["also_known_as", "biography", "overview", "production_companies", "belongs_to_collection", "homepage"]
DECIMAL_KEYS = ["popularity", "vote_average"]

dynamodb = boto3.resource("dynamodb")
table = dynamodb.Table("flopOrNot")


def saveData(json):
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

    # save the item to the database
    table.put_item(
        Item=json
    )


def getMovieDBUrl(suffix):
    """Get the URL for themoviedb API given the suffix"""
    return "https://api.themoviedb.org/3/" + suffix + "?api_key=670a2f04173250a43eca59d9e2c922d9"


def fetchAndSaveItem(url, itemPrefix):
    response = requests.get(url)
    
    if response.status_code == 200:
        jsonData = json.loads(response.text)
        itemId = itemPrefix + "-" + str(jsonData["id"])
        jsonData["itemId"] = itemId
        jsonData["relatedItemId"] = itemId
        saveData(jsonData)
    else:
        print("No data returned for " + url)


def fetchAndSavePersonById(personId):
    fetchAndSaveItem(getMovieDBUrl("person/" + str(personId)), "person")

    
def fetchAndSaveMovieById(movieId):
    fetchAndSaveItem(getMovieDBUrl("movie/" + str(movieId)), "movie")


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
                    "credit_id": str(castMember["credit_id"] )
                })
        else:
            print("No cast data for " + str(movieId))
        
        # add each crew member
        if "crew" in jsonData:
            for crewMember in jsonData["crew"]:
                saveData({
                    "itemId": "person-" + str(crewMember["id"]),
                    "relatedItemId": "movie-" + str(movieId),
                    "job": crewMember["job"],
                    "credit_id": str(crewMember["credit_id"])
                })
        else:
            print("No crew data for " + str(movieId))                
    else:
        print("Issue fetching crew/cast data for movie with id " + str(movieId))

def main():    
    startRange = 20
    for i in range(startRange, startRange + 10):
        fetchAndSaveMovieById(i)
        fetchAndSavePersonById(i)
        fetchAndSaveMovieCrewData(i)
        print("Completed fetching and loading on index " + str(i))
        
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
