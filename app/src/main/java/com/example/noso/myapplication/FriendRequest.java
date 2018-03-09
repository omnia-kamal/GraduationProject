package com.example.noso.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.noso.myapplication.Interfaces.FriendsClient;
import com.example.noso.myapplication.beans.UserId;
import com.example.noso.myapplication.beans.Users;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Users: special
 * Date: 13-12-22
 * Time: 下午3:26
 * Mail: specialcyci@gmail.com
 */
public class FriendRequest extends Fragment {

    List<Users> users;
    private View parentView;
    private ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        parentView = inflater.inflate(R.layout.friend_requests, container, false);
        listView = (ListView) parentView.findViewById(R.id.friendsRequests);
        initView();
        return parentView;
    }

    private void initView() {

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:3000/")
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();
        final FriendsClient client = retrofit.create(FriendsClient.class);
        Call<List<Users>> call = client.requests(PreferenceManager.xAuthToken);

        Log.d("homie", "onClick: " + call.toString());
        call.enqueue(new Callback<List<Users>>() {
            @Override
            public void onResponse(Call<List<Users>> call, Response<List<Users>> response) {
                users = response.body();
                Log.d("homie", "onResponse: " + users.size());
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1);

                List<String> names = new ArrayList<String>();
                for (int i = 0; i < users.size(); i++) {
                    names.add(users.get(i).getUsername() + "\n" + users.get(i).getEmail());
                }

                arrayAdapter.addAll(names);

                listView.setAdapter(arrayAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, final int pos, long l) {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                        alertDialog.setTitle("Accept friend request?");
                        alertDialog.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //TODO: approve friend request
                                Log.d("homie", "onClick: AddFriend " + pos + " " + users.get(pos).getId());
                                Call<Users> call = client.approveFriend(PreferenceManager.xAuthToken, new UserId(users.get(pos).getId()));
                                call.enqueue(new Callback<Users>() {
                                    @Override
                                    public void onResponse(Call<Users> call, Response<Users> response) {
                                        Users users = response.body();
                                        Log.d("homie", "onResponse: Add Friend response is null? " + (users == null));
                                        Log.d("homie", "onClick: AddFriend " + response.message());
                                    }

                                    @Override
                                    public void onFailure(Call<Users> call, Throwable t) {

                                    }
                                });
                            }
                        });
                        alertDialog.setNegativeButton("Reject", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //TODO: reject friend request
                                Call<Users> call = client.rejectFriend(PreferenceManager.xAuthToken, new UserId(users.get(pos).getId()));
                                call.enqueue(new Callback<Users>() {
                                    @Override
                                    public void onResponse(Call<Users> call, Response<Users> response) {
                                        Users users = response.body();
                                        Log.d("homie", "onResponse: Add Friend response is null? " + (users == null));
                                        Log.d("homie", "onClick: AddFriend " + response.message());
                                    }

                                    @Override
                                    public void onFailure(Call<Users> call, Throwable t) {

                                    }
                                });
                            }
                        });
                        AlertDialog dialog = alertDialog.create();
                        dialog.show();
                    }
                });
            }

            @Override
            public void onFailure(Call<List<Users>> call, Throwable t) {

            }
        });
    }

}
