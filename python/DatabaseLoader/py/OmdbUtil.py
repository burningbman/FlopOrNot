import requests

OMDB_URL = "http://www.omdbapi.com/?apikey=85704252&i=tt0206341"
KEYS = ["Rated", "Ratings", "Metascore", "imdbRating", "imdbVotes", "BoxOffice", "Production"]

def getImdbData(imdbId):
    response = requests.get(OMDB_URL + str(imdbId))
    data = {}
    
    if response.status_code == 200:
        for key in KEYS:
            if (response[key]):
                data[key] = response[key]
    return data