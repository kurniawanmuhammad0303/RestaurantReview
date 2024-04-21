package com.example.restaurantreview.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.restaurantreview.data.response.CustomerReviewsItem;
import com.example.restaurantreview.data.response.Restaurant;
import com.example.restaurantreview.data.response.RestaurantResponse;
import com.example.restaurantreview.data.retrofit.ApiConfig;
import com.example.restaurantreview.databinding.ActivityMainBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private static final String TAG = "MainActivity";
    private static final String RESTAURANT_ID = "uewq1zg2zlskfw1e867";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupRecyclerView();
        setupWindowInsetsListener();
        loadRestaurantData();

        binding.btnSend.setOnClickListener(view -> {
            if (binding.edReview.getText() != null) {
                postReview(binding.edReview.getText().toString());
            }
        });
    }

    private void setupRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.rvReview.setLayoutManager(layoutManager);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(this, layoutManager.getOrientation());
        binding.rvReview.addItemDecoration(itemDecoration);
    }

    private void setupWindowInsetsListener() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void loadRestaurantData() {
        showLoading(true);

        ApiConfig.getApiService().getRestaurant(RESTAURANT_ID).enqueue(new Callback<RestaurantResponse>() {
            @Override
            public void onResponse(@NotNull Call<RestaurantResponse> call, @NotNull Response<RestaurantResponse> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    setRestaurantData(response.body().getRestaurant());
                    setReviewData(response.body().getRestaurant().getCustomerReviews());
                } else {
                    Log.e(TAG, "onFailure: " + response.message());
                }
            }

            @Override
            public void onFailure(@NotNull Call<RestaurantResponse> call, @NotNull Throwable t) {
                showLoading(false);
                Log.e(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    private void setRestaurantData(Restaurant restaurant) {
        binding.tvTitle.setText(restaurant.getName());
        binding.tvDescription.setText(restaurant.getDescription());
        Glide.with(this)
                .load("https://restaurant-api.dicoding.dev/images/large/" + restaurant.getPictureId())
                .into(binding.ivPicture);
    }

    private void setReviewData(List<CustomerReviewsItem> customerReviews) {
        ArrayList<String> listReview = new ArrayList<>();
        for (CustomerReviewsItem review : customerReviews) {
            listReview.add(review.getReview() + "\n- " + review.getName());
        }

        ReviewAdapter adapter = new ReviewAdapter(listReview);
        binding.rvReview.setAdapter(adapter);
        binding.edReview.setText("");
    }

    private void postReview(String review) {
        showLoading(true);
        // Simpan kode untuk mengirim ulasan ke server di sini
        // Anda dapat menggunakan Retrofit atau metode lain untuk melakukan ini
        // Setelah menerima respon dari server, tampilkan pesan yang diisi oleh pengguna
        // Misalnya:
        String userReviewMessage = "Ulasan Anda telah berhasil dikirim: " + review;
        // Tampilkan pesan di antarmuka pengguna
        binding.tvReview.setVisibility(View.VISIBLE);
        binding.tvReview.setText(userReviewMessage);

        // Atau, jika terjadi kesalahan dalam mengirim ulasan, Anda dapat menampilkan pesan kesalahan
        // Misalnya:
        /* String errorMessage = "Terjadi kesalahan dalam mengirim ulasan. Silakan coba lagi.";
        binding.tvUserReview.setVisibility(View.VISIBLE);
        binding.tvUserReview.setText(errorMessage); */

        // Akhiri loading setelah menampilkan pesan
        showLoading(false);
    }

    private void showLoading(boolean isLoading) {
        binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }
}
