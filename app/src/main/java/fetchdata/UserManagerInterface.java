package fetchdata;

import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface UserManagerInterface {

    /*
     * @FormUrlEncoded : Point out this method will construct a form submit action.
     * @POST : Point out the form submit will use post method, the form action url is the parameter of @POST annotation.
     * @Field("form_field_name") : Indicate the form filed name, the filed value will be assigned to input parameter userNameValue.
     * */
    @FormUrlEncoded
    @POST("checkotp")
    public Call<ResponseBody> registerUser(@Field("mobileno") String emailValue);


    /*
     *  @GET : Indicate this method will perform a http get action with the specified url.
     *  @Query("userName") : Parse out the userName query parameter in the url and
     *  assign the parsed out value to userNameValue parameter.
     * */
    @GET("checkotp")
    public Call<UserDTO> getUserByName(@Query("mobileno") String mobileno);


    /*
     *  Similar with getUserByName method, this method will return all users in a list.
     * */
    @GET("checkotp")
    public Call<List<UserDTO>> getUserAllList();

    @FormUrlEncoded
    @POST("verify_otp")
    Call<ResponseBody> registermobileno(@FieldMap Map<String, String> en_otp);

    @FormUrlEncoded
    @POST("get_emergencycontactbyid")
    Call<ResponseBody> getemergencycontact(@Field("custid") String custid);

    @FormUrlEncoded
    @POST("get_customer_detials")
    Call<ResponseBody> customerinfo(@FieldMap Map<String, String> custinfo);

    @FormUrlEncoded
    @POST("add_user_emergency_contact")
    Call<ResponseBody> addsoscustomer(@FieldMap Map<String, String> custadd);

    @FormUrlEncoded
    @POST("save_refers")
    Call<ResponseBody> referus(@FieldMap Map<String, String> referus);

    @FormUrlEncoded
    @POST("sos_request")
    public Call<ResponseBody> soshit(@Field("custid") String custid);
}