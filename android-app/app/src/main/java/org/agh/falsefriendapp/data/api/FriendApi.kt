package org.agh.falsefriendapp.data.api

import org.agh.falsefriendapp.data.model.network.FriendActionRequest
import org.agh.falsefriendapp.data.model.network.FriendRequestResponse
import org.agh.falsefriendapp.data.model.network.UserIdsResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface FriendApi {
    @GET("friends")
    suspend fun getFriends(): UserIdsResponse

    @GET("friends/requests")
    suspend fun getRequests(): UserIdsResponse

    @POST("friends/requests")
    suspend fun sendRequest(
        @Query("to_user_id") userId: Int
    ): FriendRequestResponse

    @PUT("friends/requests/{id}")
    suspend fun respondToRequest(
        @Path("id") userId: Int,
        @Body body: FriendActionRequest
    )

    @DELETE("friends/{id}")
    suspend fun removeFriend(
        @Path("id") userId: Int
    )
}
