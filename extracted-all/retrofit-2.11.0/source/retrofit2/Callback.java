package retrofit2;

public interface Callback<T> {
   void onResponse(Call<T> var1, Response<T> var2);

   void onFailure(Call<T> var1, Throwable var2);
}
