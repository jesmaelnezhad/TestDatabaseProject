#curl --cookie cookies.txt --cookie-jar cookies.txt -s -i -d "login_type=basestation&username=basestation&password=basestationpassword" http://localhost:8080/TestDatabaseProject/signin
#curl --cookie cookies.txt --cookie-jar cookies.txt -s -i -d "city_id=1" http://localhost:8080/TestDatabaseProject/selectcity
#curl --cookie cookies.txt --cookie-jar cookies.txt -s -i -d "id=125445743&full=True&t1=12:34:45&t2=12:59:20" http://localhost:8080/TestDatabaseProject/updatesensors

curl -c cookies.txt -s -i -d "login_type=basestation&username=basestation&password=basestationpassword" http://localhost:8080/TestDatabaseProject/signin
curl -b cookies.txt -s -i -d "city_id=1" http://localhost:8080/TestDatabaseProject/selectcity
curl -b cookies.txt -s -i -d "id=1254453&full=true&t1=12:34:45&t2=12:59:20" http://localhost:8080/TestDatabaseProject/updatesensors

