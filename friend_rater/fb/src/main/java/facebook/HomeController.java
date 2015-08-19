package facebook;

import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Version;
import com.restfb.json.JsonArray;
import com.restfb.json.JsonObject;
import com.restfb.types.User;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.FacebookApi;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;


@Controller
public class HomeController {

  /*  //Declaring the Variables
    private static final Token EMPTY_TOKEN = null;
    private static final String code = null;
    public static final String clientId = "";
    public static final String clientSecret = "";
    public static final String STATE = "state";
    private String applicationHost = "http://localhost:8080";

    //Scribe Object
    private OAuthService oAuthService;

    //Creating Facebook Client Object
    FacebookClient fbClient = new DefaultFacebookClient(Version.VERSION_2_2);

    //Constructor
    public HomeController() {
        this.FacebookScribeAuthenticator(clientId, clientSecret, applicationHost);
    }

    //Initial Login Page
    @RequestMapping("/login/facebook")
    public String LoginPage(){
        return "login";
    }

    //Scribe Method For AccessTokn
    public void FacebookScribeAuthenticator (String clientId, String clientSecret, String applicationHost){
        this.applicationHost = applicationHost;
        this.oAuthService = buildoAuthService(clientId, clientSecret);
    }

    //OAuth Builder Service
    private OAuthService buildoAuthService(String clientId, String clientSecret) {
        return new ServiceBuilder()
                .apiKey(clientId)
                .apiSecret(clientSecret)
                .scope("user_photos")
                .scope("user_posts")
                .callback(applicationHost + "/auth/facebook/callback")
                .provider(FacebookApi.class)
                .build();
    }

    //================================================================
    //                  API to redirect to Facebook
    //================================================================
    @RequestMapping("/auth/facebook")
    public RedirectView startAuthentication(HttpSession session){
        String state = UUID.randomUUID().toString();
        session.setAttribute(STATE,state);
        String authorizationUrl =oAuthService.getAuthorizationUrl(EMPTY_TOKEN)+"&"+STATE+ "="+state;
        return new RedirectView(authorizationUrl);
    }

    //================================================================
    //               API to handle Callback from Facebook
    //================================================================
    @RequestMapping("/auth/facebook/callback")
    public String callback(@RequestParam("code") String code,@RequestParam(STATE) String state, HttpSession session) throws IOException {
        Token accessToken = getAccessToken(code);
        fbClient = new DefaultFacebookClient(accessToken.getToken(), Version.VERSION_2_2);
        return "logged-in";
    }

    //Method to take the accessToken provided by Facebook
    private Token getAccessToken(String code) {
        Verifier verifier = new Verifier(code);
        return oAuthService.getAccessToken(Token.empty(), verifier);
    }
*/


    private HashMap<String, Long> scoreList = new HashMap<String, Long>();

    // ------------------------------------------------------------------
    //                      API for TOP FRIENDS
    // ------------------------------------------------------------------
@RequestMapping(value = "/{user-id}/topfriends", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody List getTopFriends(@PathVariable("user-id") String id) {
        scoreList.clear();
        long scoreCounter;
        String accessToken = "CAACEdEose0cBABQNo6ZAApfGmzIF29eKIKXHyBVTBfp0TRZAM31QnXM3C7J5tjO5XZCj3Vhn6nSZANBZBRgWyrrCHFKHkvbdSO6CiwT0ISyA2ONmu4cK41ZCrEpugaUphohghRu8dtGfIAaO88f8pqqrDSjsZCWZBBGmXSyxXxXsb8jL5KZAHq9uJwXtJb5ZC9dy4E84RZB1a7bZCwZDZD";
        FacebookClient fbClient = new DefaultFacebookClient(accessToken, Version.VERSION_2_2);
        User me;
        me = fbClient.fetchObject(id, com.restfb.types.User.class);
        PersonInfo object = new PersonInfo();
        object.setName(me.getName());
        object.setId(me.getId());


        JsonObject postConnection = fbClient.fetchObject("me/posts", JsonObject.class);
        JsonObject photosConnection = fbClient.fetchObject("me/photos", JsonObject.class);

        int postSize = postConnection.getJsonArray("data").length();
        int photoSize = photosConnection.getJsonArray("data").length();

        // ------------------------------------------------------------------
        //                              POSTS
        // ------------------------------------------------------------------

        for (int i = 0; i < postSize; i++) {
            PostAttributes obj = new PostAttributes();
            List<PersonInfo> nameLikes = new ArrayList<PersonInfo>();
            List<PersonInfo> nameComments = new ArrayList<PersonInfo>();
            List<PersonInfo> nameTags = new ArrayList<PersonInfo>();
            Set<PersonInfo> hs = new HashSet<>();

            //-------LIKES--------//
            if (postConnection.getJsonArray("data").getJsonObject(i).has("likes")) {

                JsonArray nameLikesJson = postConnection.getJsonArray("data").getJsonObject(i).getJsonObject("likes").getJsonArray("data");
                for (int n = 0; n < nameLikesJson.length(); n++) {
                    PersonInfo person = new PersonInfo();
                    String temp1 = nameLikesJson.getJsonObject(n).getString("name");
                    String temp2 = nameLikesJson.getJsonObject(n).getString("id");
                    person.setId(temp2);
                    person.setName(temp1);
                    nameLikes.add(person);
                }
            }


            for(PersonInfo personInfo: nameLikes){
                if(scoreList.containsKey(personInfo.getName())){
                    long x = scoreList.get(personInfo.getName());
                    x++;
                    scoreList.put(personInfo.getName(), x);
                }
                else {
                    scoreCounter = 0;
                    scoreCounter++;
                    scoreList.put(personInfo.getName(), scoreCounter);
                }
            }



            //----------------COMMENTS-------------//
            if (postConnection.getJsonArray("data").getJsonObject(i).has("comments")) {

                JsonArray nameCommentsJson = postConnection.getJsonArray("data").getJsonObject(i).getJsonObject("comments").getJsonArray("data");
                for (int c = 0; c < nameCommentsJson.length(); c++) {
                    JsonObject fromCommentsJson = nameCommentsJson.getJsonObject(c).getJsonObject("from");
                    String temp3 = fromCommentsJson.getString("name");
                    String temp4 = fromCommentsJson.getString("id");
                    PersonInfo person = new PersonInfo();
                    person.setId(temp4);
                    person.setName(temp3);
                    nameComments.add(person);
                }
                hs.addAll(nameComments);
                nameComments.clear();
                nameComments.addAll(hs);
            }

            for(PersonInfo personInfo: nameComments){
                if(scoreList.containsKey(personInfo.getName())){
                    long x = scoreList.get(personInfo.getName());
                    x+=5;
                    scoreList.put(personInfo.getName(), x);
                }
                else
                {
                    scoreCounter = 0;
                    scoreCounter += 5;
                    scoreList.put(personInfo.getName(), scoreCounter);
                }
            }


            //---------TAGS-----------//
            if (postConnection.getJsonArray("data").getJsonObject(i).has("tags")) {

                JsonArray nameTagsJson = postConnection.getJsonArray("data").getJsonObject(i).getJsonObject("tags").getJsonArray("data");
                int k = 0;
                for (int c = 0; c < nameTagsJson.length(); c++) {
                    String temp5 = nameTagsJson.getJsonObject(c).getString("name");
                    String temp6 = nameTagsJson.getJsonObject(c).getString("id");
                    PersonInfo person = new PersonInfo();
                    person.setId(temp6);
                    person.setName(temp5);
                    nameTags.add(person);
                    k++;
                }
                obj.setNumberOfTags(k);
            }

            if(obj.getNumberOfTags() < 5) {
                for (PersonInfo personInfo : nameTags) {
                    if(scoreList.containsKey(personInfo.getName())){
                        long x = scoreList.get(personInfo.getName());
                        x+=10;
                        scoreList.put(personInfo.getName(), x);
                    }
                    else
                    {
                        scoreCounter = 0;
                        scoreCounter += 10;
                        scoreList.put(personInfo.getName(), scoreCounter);
                    }
                }
            }
        }


        // ------------------------------------------------------------------
        //                              PHOTOS
        // ------------------------------------------------------------------

        for (int i = 0; i < photoSize; i++) {

            PhotoAttributes obj = new PhotoAttributes();
            List<PersonInfo> nameLikes = new ArrayList<PersonInfo>();
            List<PersonInfo> nameComments = new ArrayList<PersonInfo>();
            Set<PersonInfo> hs = new HashSet<>();
            List<PersonInfo> nameTags = new ArrayList<PersonInfo>();
            List<PersonInfo> photoOwner = new ArrayList<PersonInfo>();

            //-------LIKES--------//
            if (photosConnection.getJsonArray("data").getJsonObject(i).has("likes")) {
                JsonArray nameLikesJson = photosConnection.getJsonArray("data").getJsonObject(i).getJsonObject("likes").getJsonArray("data");
                for (int n = 0; n < nameLikesJson.length(); n++) {
                    PersonInfo person = new PersonInfo();
                    String temp1 = nameLikesJson.getJsonObject(n).getString("name");
                    String temp2 = nameLikesJson.getJsonObject(n).getString("id");
                    person.setId(temp2);
                    person.setName(temp1);
                    nameLikes.add(person);
                }
            }

            for(PersonInfo personInfo: nameLikes){
                if(scoreList.containsKey(personInfo.getName())){
                    long x = scoreList.get(personInfo.getName());
                    x++;
                    scoreList.put(personInfo.getName(), x);
                }
                else {
                    scoreCounter = 0;
                    scoreCounter++;
                    scoreList.put(personInfo.getName(), scoreCounter);
                }
            }

            //----------------COMMENTS-------------//
            if (photosConnection.getJsonArray("data").getJsonObject(i).has("comments")) {

                JsonArray nameCommentsJson = photosConnection.getJsonArray("data").getJsonObject(i).getJsonObject("comments").getJsonArray("data");
                for (int c = 0; c < nameCommentsJson.length(); c++) {
                    JsonObject fromCommentsJson = nameCommentsJson.getJsonObject(c).getJsonObject("from");
                    String temp3 = fromCommentsJson.getString("name");
                    String temp4 = fromCommentsJson.getString("id");
                    PersonInfo person = new PersonInfo();
                    person.setId(temp4);
                    person.setName(temp3);
                    nameComments.add(person);
                }

                hs.addAll(nameComments);
                nameComments.clear();
                nameComments.addAll(hs);
            }

            for(PersonInfo personInfo: nameComments){
                if(scoreList.containsKey(personInfo.getName())){
                    long x = scoreList.get(personInfo.getName());
                    x++;
                    scoreList.put(personInfo.getName(), x);
                }
                else
                {
                    scoreCounter = 0;
                    scoreCounter++;
                    scoreList.put(personInfo.getName(), scoreCounter);
                }
            }

            //---------TAGS-----------//
            if (photosConnection.getJsonArray("data").getJsonObject(i).has("tags")) {
                JsonArray nameTagsJson = photosConnection.getJsonArray("data").getJsonObject(i).getJsonObject("tags").getJsonArray("data");
                int k = 0;

                for (int c = 0; c < nameTagsJson.length(); c++) {
                    String temp6 =" ";
                    if(nameTagsJson.getJsonObject(c).has("id")) {
                        temp6 = nameTagsJson.getJsonObject(c).getString("id");
                    }

                    String temp5 = nameTagsJson.getJsonObject(c).getString("name");

                    PersonInfo person = new PersonInfo();
                    person.setId(temp6);
                    person.setName(temp5);
                    nameTags.add(person);
                    k++;
                }
                obj.setNumberOfTags(k);
            }

            if(obj.getNumberOfTags() < 4) {
                for (PersonInfo personInfo : nameTags) {
                    if(scoreList.containsKey(personInfo.getName())){
                        long x = scoreList.get(personInfo.getName());
                        x++;
                        scoreList.put(personInfo.getName(), x);
                    }
                    else
                    {
                        scoreCounter = 0;
                        scoreCounter++;
                        scoreList.put(personInfo.getName(), scoreCounter);
                    }
                }
            }

            //-------------PhotoOwner------------//
            JsonObject photoOwnerJson = photosConnection.getJsonArray("data").getJsonObject(i).getJsonObject("from");
            String temp7 = photoOwnerJson.getString("name");
            String temp8 = photoOwnerJson.getString("id")  ;
            PersonInfo person = new PersonInfo();
            person.setId(temp8);
            person.setName(temp7);
            photoOwner.add(person);

            if(scoreList.containsKey(person.getName())){
                long x = scoreList.get(person.getName());
                x++;
                scoreList.put(person.getName(), x);
            }
            else
            {
                scoreCounter = 0;
                scoreCounter++;
                scoreList.put(person.getName(), scoreCounter);
            }
        }

        scoreList.remove(object.getName());
        sortedScores ob = new sortedScores(scoreList);
        List sortedList = ob.sortByValues(scoreList);
        return sortedList;
    }

}
