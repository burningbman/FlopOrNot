import boto3
import json
import requests
from decimal import *

crewUrl = "https://api.themoviedb.org/3/movie/2/credits?api_key=670a2f04173250a43eca59d9e2c922d9"
movieInfo = "https://api.themoviedb.org/3/movie/2?api_key=670a2f04173250a43eca59d9e2c922d9&language=en-US"
personInfo = "https://api.themoviedb.org/3/person/1?api_key=670a2f04173250a43eca59d9e2c922d9&language=en-US"

RESULTS_PER_PAGE = 20
IGNORED_KEYS = ["also_known_as", "biography", "overview", "production_companies", "belongs_to_collection", "homepage"]
DECIMAL_KEYS = ["popularity", "vote_average"]

dynamodb = boto3.resource("dynamodb")
table = dynamodb.Table("flopOrNot")

getcontext().prec = 6
getcontext().rounding = ROUND_UP

def saveData(json):
    for key in IGNORED_KEYS:
        json.pop(key, None)
        
    for key in DECIMAL_KEYS:
        if key in json:
            json[key] = Decimal(json[key]).quantize(Decimal('1.000'))
    
    keysToPop = []
    for key in json:
        if (json[key] == ""):
            keysToPop.append(key)
            
    for key in keysToPop:
        json.pop(key)

    table.batch_writer().put_item(
        Item=json
    )


def getMovieDBUrl(suffix):
    return "https://api.themoviedb.org/3/" + suffix + "?api_key=670a2f04173250a43eca59d9e2c922d9"


def saveFetchedMovieData(iJsonData, iActorId):
    for i in range (0, min(RESULTS_PER_PAGE, len(iJsonData["results"]))):
        movieData = iJsonData["results"][i]
        saveData({
            "itemId": "person-" + str(iActorId),
            "relatedItemId": "movie-" + str(movieData["id"]),
            "role": "actor"
        })

        
def retrieveAndSaveMovieDataByPage(iActorId, iPageNum):
    response = requests.get(getMovieDBUrl("discover/movie") + "&with_cast=" + str(iActorId) + "&page=" + str(iPageNum))
    jsonData = json.loads(response.text)
    saveFetchedMovieData(jsonData, iActorId)
    return jsonData.get("total_results")


def fetchAndSaveItem(url, itemPrefix):
    response = requests.get(url)
    
    if (response.status_code == 200):
        jsonData = json.loads(response.text)
        itemId = itemPrefix + "-" + str(jsonData["id"])
        jsonData["itemId"] = itemId
        jsonData["relatedItemId"] = itemId
        saveData(jsonData)


def fetchAndSavePersonById(personId):
    fetchAndSaveItem(getMovieDBUrl("person/" + str(personId)), "person")

    
def fetchAndSaveMovieById(movieId):
    fetchAndSaveItem(getMovieDBUrl("movie/" + str(movieId)), "movie")


def main():    
    for i in range(1, 10):
        fetchAndSaveMovieById(i)
        fetchAndSavePersonById(i)

        
main()
