package com.hot.sentefinder.network;

import com.hot.sentefinder.models.FeedBack;
import com.hot.sentefinder.models.FinancialServiceProvider;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by Jamie on 4/20/2017.
 */

public interface APIClientInterface {

    @GET("overpass/finance/{searchParam}")
    Call<List<FinancialServiceProvider>> getAllFinancialServiceProviders(@Path("searchParam") String searchParam);

    @GET("overpass/borrow/{searchParam}")
    Call<List<FinancialServiceProvider>> getBorrowFinancialServiceProviders(@Path("searchParam") String searchParam);

    @GET("overpass/save/{searchParam}")
    Call<List<FinancialServiceProvider>> getSaveFinancialServiceProviders(@Path("searchParam") String searchParam);

    @GET("overpass/send/{searchParam}")
    Call<List<FinancialServiceProvider>> getSendFinancialServiceProviders(@Path("searchParam") String searchParam);

    @GET("overpass/withdraw/{searchParam}")
    Call<List<FinancialServiceProvider>> getWithdrawFinancialServiceProviders(@Path("searchParam") String searchParam);

    @POST("rating")
    Call<FeedBack> sendFeedBack(@Body FeedBack feedBack);

}
