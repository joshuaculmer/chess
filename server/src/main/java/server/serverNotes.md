

# Curl Commands


Register
curl -X POST 'http://localhost:8080/user' -d '{"username": "username","password": "password","email": "email"}'

Login:
curl -X POST 'http://localhost:8080/session' -d '{"username":"username","password":"password"}'

Logout
curl -X DELETE 'http://localhost:8080/session' -d '{"username":"username","password":"password"}'

List Games
curl -X GET 'http://localhost:8080/game'

Create game
curl -X POST 'http://localhost:8080/game' -d '{"gameName": "gameName"}'

Join Game
curl -X PUT 'http://localhost:8080/game' -d '{"playerColor": "WHITE/BLACK","gameID": 0}'

Clear DB
curl -X DELETE 'http://localhost:8080/db'


 # GSON Notes
var serializer = new Gson();

var game = new ChessGame();

// serialize to JSON
var json = serializer.toJson(game);

// deserialize back to ChessGame
game = serializer.fromJson(json, ChessGame.class);

# MYSQL Notes
mysqlsh -u root -p*password* --sql