#!/bin/sh

# --------------------------------------------------
# Monster Trading Cards Game
# --------------------------------------------------

USERNAME="kienboec"
PASSWORD="daniel"
TOKEN="authToken-82e6df48-3ca5-4428-b9cd-308d4da05bdc"

echo "CURL Testing for Monster Trading Cards Game"
echo "Syntax: MonsterTradingCards.sh [pause]"
echo "- pause: optional, if set, then script will pause after each block"
echo .


pauseFlag=0
for arg in "$@"; do
    if [ "$arg" == "pause" ]; then
        pauseFlag=1
        break
    fi
done

if [ $pauseFlag -eq 1 ]; then read -p "Press enter to continue..."; fi

# --------------------------------------------------
echo "1) Create Users (Registration)"
# Create User
curl -i -X POST http://localhost:10001/users --header "Content-Type: application/json" -d "{\"username\":\"kienboec\", \"password\":\"daniel\"}"
echo "Should return HTTP 201"
echo .
curl -i -X POST http://localhost:10001/users --header "Content-Type: application/json" -d "{\"username\":\"altenhof\", \"password\":\"markus\"}"
echo "Should return HTTP 201"
echo .
curl -i -X POST http://localhost:10001/users --header "Content-Type: application/json" -d "{\"username\":\"admin\",    \"password\":\"istrator\"}"
echo "Should return HTTP 201"
echo .

if [ $pauseFlag -eq 1 ]; then read -p "Press enter to continue..."; fi

echo "should fail:"
curl -i -X POST http://localhost:10001/users --header "Content-Type: application/json" -d "{\"username\":\"kienboec\", \"password\":\"daniel\"}"
echo "Should return HTTP 4xx - User already exists"
echo .
curl -i -X POST http://localhost:10001/users --header "Content-Type: application/json" -d "{\"username\":\"kienboec\", \"password\":\"different\"}"
echo "Should return HTTP 4xx - User already exists"
echo . 
echo .

if [ $pauseFlag -eq 1 ]; then read -p "Press enter to continue..."; fi

# --------------------------------------------------
echo "2) Login Users"
curl -i -X POST http://localhost:10001/sessions --header "Content-Type: application/json" -d "{\"username\":\"kienboec\", \"password\":\"daniel\"}"
echo "should return HTTP 200 with generated token for the user, here: kienboec-mtcgToken"
echo .
curl -i -X POST http://localhost:10001/sessions --header "Content-Type: application/json" -d "{\"username\":\"altenhof\", \"password\":\"markus\"}"
echo "should return HTTP 200 with generated token for the user, here: altenhof-mtcgToken"
echo .
curl -i -X POST http://localhost:10001/sessions --header "Content-Type: application/json" -d "{\"username\":\"admin\",    \"password\":\"istrator\"}"
echo "should return HTTP 200 with generated token for the user, here: admin-mtcgToken"
echo .

if [ $pauseFlag -eq 1 ]; then read -p "Press enter to continue..."; fi

echo "should fail:"
curl -i -X POST http://localhost:10001/sessions --header "Content-Type: application/json" -d "{\"username\":\"kienboec\", \"password\":\"different\"}"
echo "Should return HTTP 4xx - Login failed"
echo .
echo .

if [ $pauseFlag -eq 1 ]; then read -p "Press enter to continue..."; fi

# --------------------------------------------------
echo "3) create packages (done by admin)"
curl -i -X POST http://localhost:10001/packages --header "Content-Type: application/json" --header "Authorization: Bearer admin-mtcgToken" --data '{
  "name": "ExamplePackage",
  "cards": [
    {
      "card_id": "644808c2-f87a-4600-b313-122b02322fd5",
      "name": "WaterGoblin",
      "damage": 9.0,
      "element_type": "WATER",
      "category": "MONSTER",
      "user_id": 1
    },
    {
      "card_id": "4a2757d6-b1c3-47ac-b9a3-91deab093531",
      "name": "Dragon",
      "damage": 55.0,
      "element_type": "FIRE",
      "category": "MONSTER",
      "user_id": 1
    },
    {
      "card_id": "91a6471b-1426-43f6-ad65-6fc473e16f9f",
      "name": "WaterSpell",
      "damage": 21.0,
      "element_type": "WATER",
      "category": "SPELL",
      "user_id": 1
    },
    {
      "card_id": "4ec8b269-0dfa-4f97-809a-2c63fe2a0025",
      "name": "Ork",
      "damage": 55.0,
      "element_type": "GRASS",
      "category": "MONSTER",
      "user_id": 1
    },
    {
      "card_id": "f8043c23-1534-4487-b66b-238e0c3c39b5",
      "name": "WaterSpell",
      "damage": 23.0,
      "element_type": "WATER",
      "category": "SPELL",
      "user_id": 1
    }
  ]
}'

echo "Should return HTTP 201"
echo .																																																																																		 				    
curl -i -X POST http://localhost:10001/packages --header "Content-Type: application/json" --header "Authorization: Bearer admin-mtcgToken" --data '{ "name": "ExamplePackage1", "cards": [ { "card_id": "7a1d7b5e-3b34-4a31-9e9d-1a7d8e9b7f51", "name": "WaterGoblin", "damage": 12.0, "element_type": "WATER", "category": "MONSTER", "user_id": 1 }, { "card_id": "6e2b7f53-9b4a-4c6a-8f5a-1b2d2e8b1c71", "name": "Dragon", "damage": 60.0, "element_type": "FIRE", "category": "MONSTER", "user_id": 1 }, { "card_id": "4c3f7e34-8d5a-4a6e-9f2a-1c3d3e8b2b81", "name": "WaterSpell", "damage": 24.0, "element_type": "WATER", "category": "SPELL", "user_id": 1 }, { "card_id": "5b4d8f56-1a2b-4b6a-9e3a-1d4d4f8b3c91", "name": "Ork", "damage": 50.0, "element_type": "GRASS", "category": "MONSTER", "user_id": 1 }, { "card_id": "3d5e9a67-2b3c-4c7a-9f4a-1e5e5f9b4d01", "name": "FireSpell", "damage": 26.0, "element_type": "FIRE", "category": "SPELL", "user_id": 1 } ] }'; 
echo "Should return HTTP 201"; 
echo .

curl -i -X POST http://localhost:10001/packages --header "Content-Type: application/json" --header "Authorization: Bearer admin-mtcgToken" --data '{ "name": "ExamplePackage2", "cards": [ { "card_id": "8b2e8c78-4c5d-5a8b-9f6b-2e6e6f9b5e11", "name": "WaterGoblin", "damage": 13.0, "element_type": "WATER", "category": "MONSTER", "user_id": 1 }, { "card_id": "7f3d9f89-5d6e-6b9c-9e7c-2f7f7f9b6f21", "name": "Dragon", "damage": 65.0, "element_type": "FIRE", "category": "MONSTER", "user_id": 1 }, { "card_id": "5d4f8f90-6e7f-7c9d-9f8d-2g8g8f9b7g31", "name": "WaterSpell", "damage": 25.0, "element_type": "WATER", "category": "SPELL", "user_id": 1 }, { "card_id": "6e5g9g01-7f8g-8d9e-9g9e-2h9h9f9b8h41", "name": "Ork", "damage": 55.0, "element_type": "GRASS", "category": "MONSTER", "user_id": 1 }, { "card_id": "4f6h0h12-8g9h-9e0f-9h0f-2i0i0f9b9i51", "name": "FireSpell", "damage": 27.0, "element_type": "FIRE", "category": "SPELL", "user_id": 1 } ] }'; 
echo "Should return HTTP 201"; 
echo .

curl -i -X POST http://localhost:10001/packages --header "Content-Type: application/json" --header "Authorization: Bearer admin-mtcgToken" --data '{ "name": "ExamplePackage3", "cards": [ { "card_id": "9c3f9d89-5d6f-7e9c-9f7c-3i0i0f9b0j61", "name": "WaterGoblin", "damage": 14.0, "element_type": "WATER", "category": "MONSTER", "user_id": 1 }, { "card_id": "8g4h0g90-6e8h-8f0d-0g8d-3j1j1f9b1k71", "name": "Dragon", "damage": 70.0, "element_type": "FIRE", "category": "MONSTER", "user_id": 1 }, { "card_id": "6h5i0i01-7f9i-9g0e-0h9e-3k2k2f9b2l81", "name": "WaterSpell", "damage": 26.0, "element_type": "WATER", "category": "SPELL", "user_id": 1 }, { "card_id": "7i6j1j12-8g0j-0h1f-0i0f-3l3l3f9b3m91", "name": "Ork", "damage": 60.0, "element_type": "GRASS", "category": "MONSTER", "user_id": 1 }, { "card_id": "5j7k2k23-9h1k-1i2g-1j1g-3m4m4f9b4n01", "name": "FireSpell", "damage": 28.0, "element_type": "FIRE", "category": "SPELL", "user_id": 1 } ] }'; 
echo "Should return HTTP 201"; 
echo .

curl -i -X POST http://localhost:10001/packages --header "Content-Type: application/json" --header "Authorization: Bearer admin-mtcgToken" --data '{ "name": "ExamplePackage4", "cards": [ { "card_id": "0j8l3l34-ae2l-2j3h-2k2h-4n5n5f9b5o11", "name": "WaterGoblin", "damage": 15.0, "element_type": "WATER", "category": "MONSTER", "user_id": 1 }, { "card_id": "9k1m4m45-bf3m-3k4i-3l3i-4o6o6f9b6p21", "name": "Dragon", "damage": 75.0, "element_type": "FIRE", "category": "MONSTER", "user_id": 1 }, { "card_id": "7l2n5n56-cg4n-4l5j-4m4j-4p7p7f9b7q31", "name": "WaterSpell", "damage": 27.0, "element_type": "WATER", "category": "SPELL", "user_id": 1 }, { "card_id": "8m3o6o67-dh5o-5m6k-5n5k-4q8q8f9b8r41", "name": "Ork", "damage": 65.0, "element_type": "GRASS", "category": "MONSTER", "user_id": 1 }, { "card_id": "6n4p7p78-ei6p-6n7l-6o6l-4r9r9f9b9s51", "name": "FireSpell", "damage": 29.0, "element_type": "FIRE", "category": "SPELL", "user_id": 1 } ] }'; 
echo "Should return HTTP 201"; 
echo .
echo .

if [ $pauseFlag -eq 1 ]; then read -p "Press enter to continue..."; fi

# --------------------------------------------------
echo "4) acquire packages kienboec"
curl -i -X POST http://localhost:10001/transactions/packages \
  --header "Content-Type: application/json" \
  --header "Authorization: Bearer authToken-82e6df48-3ca5-4428-b9cd-308d4da05bdc" \
  -d '{}'
echo "Should return HTTP 201"
echo .
curl -i -X POST http://localhost:10001/transactions/packages --header "Content-Type: application/json" --header "Authorization: Bearer authToken-82e6df48-3ca5-4428-b9cd-308d4da05bdc" -d ""
echo "Should return HTTP 201"
echo .
curl -i -X POST http://localhost:10001/transactions/packages --header "Content-Type: application/json" --header "Authorization: Bearer authToken-82e6df48-3ca5-4428-b9cd-308d4da05bdc" -d ""
echo "Should return HTTP 201"
echo .
curl -i -X POST http://localhost:10001/transactions/packages --header "Content-Type: application/json" --header "Authorization: Bearer authToken-82e6df48-3ca5-4428-b9cd-308d4da05bdc" -d ""
echo "Should return HTTP 201"
echo .
echo "should fail (no money):"
curl -i -X POST http://localhost:10001/transactions/packages --header "Content-Type: application/json" --header "Authorization: Bearer authToken-82e6df48-3ca5-4428-b9cd-308d4da05bdc" -d ""
echo "Should return HTTP 4xx - Not enough money"
echo .
echo .

if [ $pauseFlag -eq 1 ]; then read -p "Press enter to continue..."; fi

# --------------------------------------------------
echo "5) acquire packages altenhof"
curl -i -X POST http://localhost:10001/transactions/packages --header "Content-Type: application/json" --header "Authorization: Bearer authToken-02011b90-b5b3-4ef6-a4ac-c6d4b3098bc4" -d ""
echo "Should return HTTP 201"
echo .
curl -i -X POST http://localhost:10001/transactions/packages --header "Content-Type: application/json" --header "Authorization: Bearer authToken-02011b90-b5b3-4ef6-a4ac-c6d4b3098bc4" -d ""
echo "Should return HTTP 201"
echo .
echo "should fail (no package):"
curl -i -X POST http://localhost:10001/transactions/packages --header "Content-Type: application/json" --header "Authorization: Bearer authToken-02011b90-b5b3-4ef6-a4ac-c6d4b3098bc4" -d ""
echo "Should return HTTP 4xx - No packages available"
echo .
echo .

if [ $pauseFlag -eq 1 ]; then read -p "Press enter to continue..."; fi

# --------------------------------------------------
echo "6) add new packages"
curl -i -X POST http://localhost:10001/packages --header "Content-Type: application/json" --header "Authorization: Bearer admin-mtcgToken" --data '{ "name": "ExamplePackage5", "cards": [ { "card_id": "1a2b3c4d-5e6f-7g8h-9i0j-1k2l3m4n5o6p", "name": "WaterGoblin", "damage": 16.0, "element_type": "WATER", "category": "MONSTER", "user_id": 1 }, { "card_id": "2b3c4d5e-6f7g-8h9i-0j1k-2l3m4n5o6p7q", "name": "Dragon", "damage": 77.0, "element_type": "FIRE", "category": "MONSTER", "user_id": 1 }, { "card_id": "3c4d5e6f-7g8h-9i0j-1k2l-3m4n5o6p7q8r", "name": "WaterSpell", "damage": 28.0, "element_type": "WATER", "category": "SPELL", "user_id": 1 }, { "card_id": "4d5e6f7g-8h9i-0j1k-2l3m-4n5o6p7q8r9s", "name": "Ork", "damage": 66.0, "element_type": "GRASS", "category": "MONSTER", "user_id": 1 }, { "card_id": "5e6f7g8h-9i0j-1k2l-3m4n-5o6p7q8r9s0t", "name": "FireSpell", "damage": 30.0, "element_type": "FIRE", "category": "SPELL", "user_id": 1 } ] }';
echo "Should return HTTP 201"
echo .
curl -i -X POST http://localhost:10001/packages --header "Content-Type: application/json" --header "Authorization: Bearer admin-mtcgToken" --data '{ "name": "ExamplePackage6", "cards": [ { "card_id": "6f7g8h9i-0j1k-2l3m-4n5o-6p7q8r9s0t1u", "name": "WaterGoblin", "damage": 17.0, "element_type": "WATER", "category": "MONSTER", "user_id": 1 }, { "card_id": "7g8h9i0j-1k2l-3m4n-5o6p-7q8r9s0t1u2v", "name": "Dragon", "damage": 78.0, "element_type": "FIRE", "category": "MONSTER", "user_id": 1 }, { "card_id": "8h9i0j1k-2l3m-4n5o-6p7q-8r9s0t1u2v3w", "name": "WaterSpell", "damage": 29.0, "element_type": "WATER", "category": "SPELL", "user_id": 1 }, { "card_id": "9i0j1k2l-3m4n-5o6p-7q8r-9s0t1u2v3w4x", "name": "Ork", "damage": 67.0, "element_type": "GRASS", "category": "MONSTER", "user_id": 1 }, { "card_id": "0j1k2l3m-4n5o-6p7q-8r9s-0t1u2v3w4x5y", "name": "FireSpell", "damage": 31.0, "element_type": "FIRE", "category": "SPELL", "user_id": 1 } ] }'; 
echo "Should return HTTP 201"
echo .
curl -i -X POST http://localhost:10001/packages --header "Content-Type: application/json" --header "Authorization: Bearer admin-mtcgToken" --data '{ "name": "ExamplePackage7", "cards": [ { "card_id": "6f7g8h9i-0j1k-2l3m-4n5o-6p7q8r9s0t0u", "name": "WaterGoblin", "damage": 17.0, "element_type": "WATER", "category": "MONSTER", "user_id": 1 }, { "card_id": "7g8h9i0j-1k2l-3m4n-5o6p-7q8r9s0t1u8v", "name": "Dragon", "damage": 78.0, "element_type": "FIRE", "category": "MONSTER", "user_id": 1 }, { "card_id": "8h9i0j1k-2l3m-4n5o-6p7q-8r9s0t1u2v5w", "name": "WaterSpell", "damage": 29.0, "element_type": "WATER", "category": "SPELL", "user_id": 1 }, { "card_id": "9i0j1k2l-3m4n-5o6p-7q8r-9s0t1u2v3w7x", "name": "Ork", "damage": 67.0, "element_type": "GRASS", "category": "MONSTER", "user_id": 1 }, { "card_id": "0j1k2l3m-4n5o-6p7q-8r9s-0t1u2v3w4x9y", "name": "FireSpell", "damage": 31.0, "element_type": "FIRE", "category": "SPELL", "user_id": 1 } ] }'; 
echo "Should return HTTP 201"
echo .
echo .

if [ $pauseFlag -eq 1 ]; then read -p "Press enter to continue..."; fi

# --------------------------------------------------
echo "7) acquire newly created packages altenhof"
curl -i -X POST http://localhost:10001/transactions/packages --header "Content-Type: application/json" --header "Authorization: Bearer authToken-02011b90-b5b3-4ef6-a4ac-c6d4b3098bc4" -d ""
echo "Should return HTTP 201"
echo .
curl -i -X POST http://localhost:10001/transactions/packages --header "Content-Type: application/json" --header "Authorization: Bearer authToken-02011b90-b5b3-4ef6-a4ac-c6d4b3098bc4" -d ""
echo "Should return HTTP 201"
echo .
echo "should fail (no money):"
curl -i -X POST http://localhost:10001/transactions/packages --header "Content-Type: application/json" --header "Authorization: Bearer authToken-02011b90-b5b3-4ef6-a4ac-c6d4b3098bc4" -d ""
echo "Should return HTTP 4xx - Not enough money"
echo .
echo .

if [ $pauseFlag -eq 1 ]; then read -p "Press enter to continue..."; fi

# --------------------------------------------------
echo "8) show all acquired cards kienboec"
curl -i -X GET http://localhost:10001/cards --header "Authorization: Bearer authToken-82e6df48-3ca5-4428-b9cd-308d4da05bdc"
echo "Should return HTTP 200 - and a list of all cards"
echo "should fail (no token):"
curl -i -X GET http://localhost:10001/cards 
echo "Should return HTTP 4xx - Unauthorized"
echo .
echo .

if [ $pauseFlag -eq 1 ]; then read -p "Press enter to continue..."; fi

# --------------------------------------------------
echo "9) show all acquired cards altenhof"
curl -i -X GET http://localhost:10001/cards --header "Authorization: Bearer authToken-02011b90-b5b3-4ef6-a4ac-c6d4b3098bc4"
echo "Should return HTTP 200 - and a list of all cards"
echo .
echo .

if [ $pauseFlag -eq 1 ]; then read -p "Press enter to continue..."; fi

# --------------------------------------------------
echo "10) show unconfigured deck"
curl -i -X GET http://localhost:10001/deck --header "Authorization: Bearer authToken-82e6df48-3ca5-4428-b9cd-308d4da05bdc"
echo "Should return HTTP 200 - and a empty-list"
echo .
curl -i -X GET http://localhost:10001/deck --header "Authorization: Bearer authToken-02011b90-b5b3-4ef6-a4ac-c6d4b3098bc4"
echo "Should return HTTP 200 - and a empty-list"
echo .
echo .

if [ $pauseFlag -eq 1 ]; then read -p "Press enter to continue..."; fi

# --------------------------------------------------
echo "11) configure deck"
curl -i -X PUT http://localhost:10001/deck --header "Content-Type: application/json" \
 --header "Authorization: Bearer authToken-82e6df48-3ca5-4428-b9cd-308d4da05bdc" \
 --data '[
    "25a3a3da-e79f-4793-ba5f-ad7759c2d1fb",
    "ed9b266e-b622-45cc-be20-1a48baf017e7",
    "f8e5b257-7286-484c-8ece-d47041768a5d",
    "a835fe53-656d-43e4-859e-ba7aaad7b345"
  ]'
echo "Should return HTTP 2xx"
echo .
curl -i -X GET http://localhost:10001/deck --header "Authorization: Bearer authToken-82e6df48-3ca5-4428-b9cd-308d4da05bdc"
echo "Should return HTTP 200 - and a list of all cards"
echo .
curl -i -X PUT http://localhost:10001/deck --header "Content-Type: application/json" \
 --header "Authorization: Bearer authToken-02011b90-b5b3-4ef6-a4ac-c6d4b3098bc4" \
  --data '[
    "459cdda5-15de-44d1-86aa-a630c13fbc0e",
    "454b66b8-48ae-42a6-b6e0-d87e282b3a55",
    "527f773b-32cd-4baf-8b92-5cdc8c9caea4",
    "bca4a885-8144-4e97-b3d2-917a3d9296ff"
  ]'
echo .
curl -i -X GET http://localhost:10001/deck --header "Authorization: Bearer authToken-02011b90-b5b3-4ef6-a4ac-c6d4b3098bc4"
echo "Should return HTTP 200 - and a list of all cards"
echo .
echo .

if [ $pauseFlag -eq 1 ]; then read -p "Press enter to continue..."; fi

echo "should fail and show original from before:"
curl -i -X PUT http://localhost:10001/deck --header "Content-Type: application/json" --header "Authorization: Bearer authToken-02011b90-b5b3-4ef6-a4ac-c6d4b3098bc4" -d "[\"845f0dc7-37d0-426e-994e-43fc3ac83c08\", \"99f8f8dc-e25e-4a95-aa2c-782823f36e2a\", \"e85e3976-7c86-4d06-9a80-641c2019a79f\", \"171f6076-4eb5-4a7d-b3f2-2d650cc3d237\"]"
echo "Should return HTTP 4xx"
echo .
curl -i -X GET http://localhost:10001/deck --header "Authorization: Bearer authToken-02011b90-b5b3-4ef6-a4ac-c6d4b3098bc4"
echo "Should return HTTP 200 - and a list of all cards"
echo .
echo .
echo should fail ... only 3 cards set
curl -i -X PUT http://localhost:10001/deck --header "Content-Type: application/json" --header "Authorization: Bearer authToken-02011b90-b5b3-4ef6-a4ac-c6d4b3098bc4" -d "[\"aa9999a0-734c-49c6-8f4a-651864b14e62\", \"d6e9c720-9b5a-40c7-a6b2-bc34752e3463\", \"d60e23cf-2238-4d49-844f-c7589ee5342e\"]"
echo "Should return HTTP 4xx - Bad request"
echo .

if [ $pauseFlag -eq 1 ]; then read -p "Press enter to continue..."; fi

# --------------------------------------------------
echo "12) show configured deck"
curl -i -X GET http://localhost:10001/deck --header "Authorization: Bearer authToken-82e6df48-3ca5-4428-b9cd-308d4da05bdc"
echo "Should return HTTP 200 - and a list of all cards"
echo .
curl -i -X GET http://localhost:10001/deck --header "Authorization: Bearer authToken-02011b90-b5b3-4ef6-a4ac-c6d4b3098bc4"
echo "Should return HTTP 200 - and a list of all cards"
echo .
echo .

if [ $pauseFlag -eq 1 ]; then read -p "Press enter to continue..."; fi

REM --------------------------------------------------
echo "13) show configured deck different representation"
echo kienboec
curl -i -X GET "http://localhost:10001/deck?format=plain" --header "Authorization: Bearer authToken-82e6df48-3ca5-4428-b9cd-308d4da05bdc"
echo "Should return HTTP 200 - and a list of all cards"
echo .
echo .
echo altenhof
curl -i -X GET "http://localhost:10001/deck?format=plain" --header "Authorization: Bearer authToken-02011b90-b5b3-4ef6-a4ac-c6d4b3098bc4"
echo "Should return HTTP 200 - and a list of all cards"
echo .
echo .

if [ $pauseFlag -eq 1 ]; then read -p "Press enter to continue..."; fi

# --------------------------------------------------
echo "14) edit user data"
echo .
curl -i -X GET http://localhost:10001/users/kienboec --header "Authorization: Bearer authToken-82e6df48-3ca5-4428-b9cd-308d4da05bdc"
echo "Should return HTTP 200 - and current user data"
echo .
curl -i -X GET http://localhost:10001/users/altenhof --header "Authorization: Bearer authToken-02011b90-b5b3-4ef6-a4ac-c6d4b3098bc4"
echo "Should return HTTP 200 - and current user data"
echo .
curl -i -X PUT http://localhost:10001/users/kienboec --header "Content-Type: application/json" --header "Authorization: Bearer kienboec-mtcgToken" -d "{\"Name\": \"Kienboeck\",  \"Bio\": \"me playin...\", \"Image\": \":-)\"}"
echo "Should return HTTP 2xx"
echo .
curl -i -X PUT http://localhost:10001/users/altenhof --header "Content-Type: application/json" --header "Authorization: Bearer authToken-02011b90-b5b3-4ef6-a4ac-c6d4b3098bc4" -d "{\"Name\": \"Altenhofer\", \"Bio\": \"me codin...\",  \"Image\": \":-D\"}"
echo "Should return HTTP 2xx"
echo .
curl -i -X GET http://localhost:10001/users/kienboec --header "Authorization: Bearer authToken-82e6df48-3ca5-4428-b9cd-308d4da05bdc"
echo "Should return HTTP 200 - and new user data"
echo .
curl -i -X GET http://localhost:10001/users/altenhof --header "Authorization: Bearer authToken-02011b90-b5b3-4ef6-a4ac-c6d4b3098bc4"
echo "Should return HTTP 200 - and new user data"
echo .
echo .

if [ $pauseFlag -eq 1 ]; then read -p "Press enter to continue..."; fi

echo "should fail:"
curl -i -X GET http://localhost:10001/users/altenhof --header "Authorization: Bearer kienboec-mtcgToken"
echo "Should return HTTP 4xx"
echo .
curl -i -X GET http://localhost:10001/users/kienboec --header "Authorization: Bearer altenhof-mtcgToken"
echo "Should return HTTP 4xx"
echo .
curl -i -X PUT http://localhost:10001/users/kienboec --header "Content-Type: application/json" --header "Authorization: Bearer altenhof-mtcgToken" -d "{\"Name\": \"Hoax\",  \"Bio\": \"me playin...\", \"Image\": \":-)\"}"
echo "Should return HTTP 4xx"
echo .
curl -i -X PUT http://localhost:10001/users/altenhof --header "Content-Type: application/json" --header "Authorization: Bearer kienboec-mtcgToken" -d "{\"Name\": \"Hoax\", \"Bio\": \"me codin...\",  \"Image\": \":-D\"}"
echo "Should return HTTP 4xx"
echo .
curl -i -X GET http://localhost:10001/users/someGuy  --header "Authorization: Bearer kienboec-mtcgToken"
echo "Should return HTTP 4xx"
echo .
echo .

if [ $pauseFlag -eq 1 ]; then read -p "Press enter to continue..."; fi

# --------------------------------------------------
echo "15) stats"
curl -i -X GET http://localhost:10001/stats --header "Authorization: Bearer authToken-82e6df48-3ca5-4428-b9cd-308d4da05bdc"
echo "Should return HTTP 200 - and user stats"
echo .
curl -i -X GET http://localhost:10001/stats --header "Authorization: Bearer authToken-02011b90-b5b3-4ef6-a4ac-c6d4b3098bc4"
echo "Should return HTTP 200 - and user stats"
echo .
echo .

if [ $pauseFlag -eq 1 ]; then read -p "Press enter to continue..."; fi

# --------------------------------------------------
echo "16) scoreboard"
curl -i -X GET http://localhost:10001/scoreboard --header "Authorization: authToken-82e6df48-3ca5-4428-b9cd-308d4da05bdc"
echo "Should return HTTP 200 - and the scoreboard"
echo .
echo .

if [ $pauseFlag -eq 1 ]; then read -p "Press enter to continue..."; fi

# --------------------------------------------------
echo "17) battle"
curl -i -X POST http://localhost:10001/battles \
 --header "Authorization: Bearer authToken-82e6df48-3ca5-4428-b9cd-308d4da05bdc" \
--data '{"opponentId": 17}'  &
curl -i -X POST http://localhost:10001/battles \
 --header "Authorization: Bearer authToken-02011b90-b5b3-4ef6-a4ac-c6d4b3098bc4" \
--data '{"opponentId": 16}'  &
wait\
 

if [ $pauseFlag -eq 1 ]; then read -p "Press enter to continue..."; fi

# --------------------------------------------------
echo "18) Stats"
echo "kienboec"
curl -i -X GET http://localhost:10001/stats --header "Authorization: Bearer authToken-82e6df48-3ca5-4428-b9cd-308d4da05bdc"
echo "Should return HTTP 200 - and changed user stats"
echo .
echo altenhof
curl -i -X GET http://localhost:10001/stats --header "Authorization: Bearer authToken-02011b90-b5b3-4ef6-a4ac-c6d4b3098bc4"
echo "Should return HTTP 200 - and changed user stats"
echo .
echo .

if [ $pauseFlag -eq 1 ]; then read -p "Press enter to continue..."; fi

# --------------------------------------------------
echo "19) scoreboard"
curl -i -X GET http://localhost:10001/scoreboard --header "Authorization: Bearer authToken-82e6df48-3ca5-4428-b9cd-308d4da05bdc"
echo "Should return HTTP 200 - and the changed scoreboard"
echo .
echo .

if [ $pauseFlag -eq 1 ]; then read -p "Press enter to continue..."; fi

# --------------------------------------------------
echo "20) trade"
echo "check trading deals"
curl -i -X GET http://localhost:10001/tradings --header "Authorization: Bearer authToken-82e6df48-3ca5-4428-b9cd-308d4da05bdc"
echo "Should return HTTP 200 - and an empty list"
echo .
echo create trading deal
curl -i -X POST http://localhost:10001/tradings --header "Content-Type: application/json" --header "Authorization: Bearer kienboec-mtcgToken" -d "{\"Id\": \"6cd85277-4590-49d4-b0cf-ba0a921faad0\", \"CardToTrade\": \"1cb6ab86-bdb2-47e5-b6e4-68c5ab389334\", \"Type\": \"monster\", \"MinimumDamage\": 15}"
echo "Should return HTTP 201"
echo .

if [ $pauseFlag -eq 1 ]; then read -p "Press enter to continue..."; fi

echo "check trading deals"
curl -i -X GET http://localhost:10001/tradings --header "Authorization: Bearer kienboec-mtcgToken"
echo "Should return HTTP 200 - and the trading deal"
echo .
curl -i -X GET http://localhost:10001/tradings --header "Authorization: Bearer altenhof-mtcgToken"
echo "Should return HTTP 200 - and the trading deal"
echo .

if [ $pauseFlag -eq 1 ]; then read -p "Press enter to continue..."; fi

echo "delete trading deals"
curl -i -X DELETE http://localhost:10001/tradings/6cd85277-4590-49d4-b0cf-ba0a921faad0 --header "Authorization: Bearer kienboec-mtcgToken"
echo "Should return HTTP 2xx"
echo .
echo .

if [ $pauseFlag -eq 1 ]; then read -p "Press enter to continue..."; fi

# --------------------------------------------------
echo "21) check trading deals"
curl -i -X GET http://localhost:10001/tradings  --header "Authorization: Bearer kienboec-mtcgToken"
echo "Should return HTTP 200 ..."
echo .
curl -i -X POST http://localhost:10001/tradings --header "Content-Type: application/json" --header "Authorization: Bearer kienboec-mtcgToken" -d "{\"Id\": \"6cd85277-4590-49d4-b0cf-ba0a921faad0\", \"CardToTrade\": \"1cb6ab86-bdb2-47e5-b6e4-68c5ab389334\", \"Type\": \"monster\", \"MinimumDamage\": 15}"
echo "Should return HTTP 201"
echo check trading deals
curl -i -X GET http://localhost:10001/tradings  --header "Authorization: Bearer kienboec-mtcgToken"
echo "Should return HTTP 200 ..."
echo .
curl -i -X GET http://localhost:10001/tradings  --header "Authorization: Bearer altenhof-mtcgToken"
echo "Should return HTTP 200 ..."
echo .

if [ $pauseFlag -eq 1 ]; then read -p "Press enter to continue..."; fi

echo "try to trade with yourself (should fail)"
curl -i -X POST http://localhost:10001/tradings/6cd85277-4590-49d4-b0cf-ba0a921faad0 --header "Content-Type: application/json" --header "Authorization: Bearer kienboec-mtcgToken" -d "\"4ec8b269-0dfa-4f97-809a-2c63fe2a0025\""
echo "Should return HTTP 4xx"
echo .

if [ $pauseFlag -eq 1 ]; then read -p "Press enter to continue..."; fi

echo "try to trade"
echo .
curl -i -X POST http://localhost:10001/tradings/6cd85277-4590-49d4-b0cf-ba0a921faad0 --header "Content-Type: application/json" --header "Authorization: Bearer altenhof-mtcgToken" -d "\"951e886a-0fbf-425d-8df5-af2ee4830d85\""
echo "Should return HTTP 201 ..."
echo .
curl -i -X GET http://localhost:10001/tradings --header "Authorization: Bearer kienboec-mtcgToken"
echo "Should return HTTP 200 ..."
echo .
curl -i -X GET http://localhost:10001/tradings --header "Authorization: Bearer altenhof-mtcgToken"
echo "Should return HTTP 200 ..."
echo .

# --------------------------------------------------
echo "end..."
