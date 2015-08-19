*******FACEBOOK FRIEND RATER*******

How to run in command prompt:
1. Navigate to directory friend-rater-demo/friend-rater/fb
2. Run "gradle build"
3. Run "gradle run"
4. Launch advanced rest client extension in Chrome
5. In the rest client, the request header has to be: http://localhost:8080/me/topfriends and the request method: GET
6. Top 5 friends with their scores will be displayed.

NOTE: string variable accessToken in HomeController.java needs to be changed before running the app. Go to Graph API 
explorer in developers.facebook.com,copy the access token given there and paste it in the variable accessToken in 
HomeController.java 