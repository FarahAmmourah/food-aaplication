//package com.farah.foodapp.network;
//
//import com.farah.foodapp.network.models.ApiResponse;
//import com.farah.foodapp.network.models.LoginRequest;
//import com.farah.foodapp.network.models.RegisterRequest;
//import com.farah.foodapp.network.models.ForgotRequest;
//
//import retrofit2.Call;
//import retrofit2.http.Body;
//import retrofit2.http.POST;
//
//public interface ApiService {
//
//    // ✅ Login: /api/auth/login/
//    @POST("api/auth/login/")
//    Call<ApiResponse> login(@Body LoginRequest request);
//
//    // ✅ Register: /api/auth/registration/
//    @POST("api/auth/registration/")
//    Call<ApiResponse> register(@Body RegisterRequest request);
//
//    // ✅ Forgot password: /api/auth/password/reset/
//    @POST("api/auth/password/reset/")
//    Call<ApiResponse> forgotPassword(@Body ForgotRequest request);
//}
