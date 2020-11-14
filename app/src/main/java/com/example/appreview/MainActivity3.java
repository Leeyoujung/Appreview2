package com.example.appreview;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity3 extends AppCompatActivity {

    // 서버에서 가져올 정보를 담을 변수 선언

    String myJSON;
    private static final String TAG_RESULTS="result";
    private static final String TAG_SID = "sid";
    private static final String TAG_PHONE = "phone";
    private static final String TAG_Address ="address";

    JSONArray store = null;


    // 서버 정보를 담을 배열(ArrayList)

    ArrayList<HashMap<String, String>> storeList;

    ImageView imageView1;
    ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        imageView1 = (ImageView) findViewById(R.id.imageView1);
        list = (ListView) findViewById(R.id.listView);
        storeList = new ArrayList<HashMap<String,String>>();
        getDbData("http://IP주소/mobile/get_json.php");
    }

    public void setImage(int resId) {
        imageView1.setImageResource(resId);
    }

    private void getDbData(String string) {
        class GetDataJSON extends AsyncTask<String, Void, String>{

            @Override
            protected String doInBackground(String... params) {

                String uri = params[0];
                BufferedReader bufferedReader = null;



                try {
                    URL url = new URL(uri);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection(); // 연결 객체 생성
                    StringBuilder sb = new StringBuilder();

                    if(conn != null){ // 연결되었으면
                        conn.setConnectTimeout(10000);
                        conn.setUseCaches(false);
                        if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){ // 연결 코드가 리턴되면
                            bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                            String json;
                            while((json = bufferedReader.readLine())!= null){
                                sb.append(json + "\n");
                            }
                        }
                    }
                    return sb.toString().trim();
                } catch(Exception e){
                    return new String("Exception: " + e.getMessage());
                }
            }


            protected void onPostExecute(String result){
                myJSON=result;
                showList();
            }
        }

        GetDataJSON g = new GetDataJSON();
        g.execute(string);

    }

    protected void showList(){
        try {
            JSONObject jsonObj = new JSONObject(myJSON);
            store = jsonObj.getJSONArray(TAG_RESULTS);

            for(int i=0;i<store.length();i++){
                JSONObject c = store.getJSONObject(i);
                String sid = c.getString(TAG_SID);
                String phone = c.getString(TAG_PHONE);
                String address = c.getString(TAG_Address);

                HashMap<String,String> persons = new HashMap<String,String>();

                persons.put(TAG_SID,sid);
                persons.put(TAG_PHONE,phone);
                persons.put(TAG_Address,address);

                storeList.add(persons);
            }

            // 어댑터 생성, R.layout.list_item : Layout ID
            ListAdapter adapter = new SimpleAdapter(
                    MainActivity3.this, storeList, R.layout.list_item,
                    new String[]{TAG_SID,TAG_PHONE,TAG_Address},
                    new int[]{R.id.sid, R.id.phone, R.id.address}
            );

            list.setAdapter(adapter); // ListView 에 어댑터 설정(연결)

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}