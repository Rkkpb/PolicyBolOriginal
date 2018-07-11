package fetchdata;

import retrofit2.Converter;
import retrofit2.Retrofit;

public class UserManager {

    private static String baseUrl = "http://45.35.62.217/crm/";

    public static UserManagerInterface getUserManagerService(Converter.Factory converterFactory)
    {
        // Create retrofit builder.
        Retrofit.Builder retrofitBuilder = new Retrofit.Builder();

        // Set base url. All the @POST @GET url is relative to this url.
        retrofitBuilder.baseUrl(baseUrl);

        /* The converter factory can be GsonConverterFactory( Convert response text to json object. ),
           if the value is null then convert response text okhttp3.ResponseBody. */
        if(converterFactory != null ) {
            retrofitBuilder.addConverterFactory(converterFactory);
        }

        // Build the retrofit object.
        Retrofit retrofit = retrofitBuilder.build();

        //Create instance for user manager interface and return it.
        UserManagerInterface userManagerService = retrofit.create(UserManagerInterface.class);
        return userManagerService;
    }
}