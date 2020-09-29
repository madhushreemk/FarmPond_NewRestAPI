package df.farmponds;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import df.farmponds.Models.ClusterAcademicEmployee;
import df.farmponds.Models.ClusterAcademicList;
import df.farmponds.Models.ClusterAcademicSummary;
import df.farmponds.Models.ClusterList;
import df.farmponds.Models.ClusterSummaryList;
import df.farmponds.Models.DefaultResponse;
import df.farmponds.Models.ErrorUtils;
import df.farmponds.Models.GetAppVersion;
import df.farmponds.Models.GetAppVersionList;
import df.farmponds.remote.Class_ApiUtils;
import df.farmponds.remote.Interface_userservice;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminClustersActivity extends AppCompatActivity {


    Spinner yearlist_farmer_SPl;
    Interface_userservice userService1;
    String str_farmerID, str_employee_id;
    String Type,YearId,Name;


    public static final String sharedpreferencebook_usercredential = "sharedpreferencebook_usercredential";
    public static final String KeyValue_employeeid = "KeyValue_employeeid";
    public static final String KeyValue_employeecategory = "KeyValue_employeecategory";
    SharedPreferences sharedpreferencebook_usercredential_Obj;

    public static final String sharedpreferenc_username = "googlelogin_name";
    public static final String Key_username = "name_googlelogin";
    SharedPreferences sharedpref_userimage_Obj;
    public static final String sharedpreferenc_userimage = "googlelogin_img";
    public static final String key_userimage = "profileimg_googlelogin";
    SharedPreferences sharedpref_username_Obj;

    ClusterAcademicEmployee[] cluster_dataLists;
    ClusterList class_cluster_dataList = new ClusterList();
    ClusterAcademicList[] arrayObj_Class_yearDetails2;

    ClusterAcademicList Obj_Class_yearDetails;
    String selected_year, sp_stryear_ID;

    Class_InternetDectector internetDectector;
    Boolean isInternetPresent = false;

    ClusterAcademicSummary[] cluster_summaryLists;
    ClusterSummaryList class_cluster_summaryList = new ClusterSummaryList();
    ClusterSummaryList[] arraysummarylist;

    AdminListViewAdapter clusterListViewAdapter;
    private ArrayList<ClusterSummaryList> clusterList;
    private ArrayList<ClusterSummaryList> originalList = null;
    private EditText etSearch;
    private ListView lview,listview_summarylist;
    Toolbar toolbar;
    private String versioncode;
    TextView userName;
    String str_Googlelogin_Username;

    ClusterSummaryListViewAdapter summaryListViewAdapter;
    private ArrayList<ClusterSummaryList> summaryList;
    private ArrayList<ClusterSummaryList> summaryoriginalList = null;

    String str_flag;
    SharedPreferences sharedpref_flag_Obj;
    public static final String sharedpreferenc_flag = "flag_sharedpreference";
    public static final String key_flag = "flag";

    String Employee_Role;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cluster_home);

        toolbar = (Toolbar) findViewById(R.id.toolbar_n_actionbar);
        setSupportActionBar(toolbar);
      //  getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TextView title = (TextView) toolbar.findViewById(R.id.title_name);
        title.setText("Cluster List");
        getSupportActionBar().setTitle("");

        userService1 = Class_ApiUtils.getUserService();
        yearlist_farmer_SPl=(Spinner) findViewById(R.id.yearlist_farmer_SP);
        lview = (NonScrollListView) findViewById(R.id.listview_clusterlist);
        etSearch = (EditText) findViewById(R.id.etSearch);
        userName = (TextView) findViewById(R.id.userName);
        listview_summarylist=(NonScrollListView) findViewById(R.id.listview_summarylist);

        sharedpreferencebook_usercredential_Obj=getSharedPreferences(sharedpreferencebook_usercredential, Context.MODE_PRIVATE);
        Employee_Role=sharedpreferencebook_usercredential_Obj.getString(KeyValue_employeecategory, "").trim();

        Log.e("tag","Employee_Role="+Employee_Role);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            str_employee_id = extras.getString("EmployeeId");
            Log.e("tag", "2nd str_employee_id=" + str_employee_id);
            Type = extras.getString("Type");
            YearId = extras.getString("YearId");
            Name = extras.getString("Name");
        }

        userName.setText(Name);

        internetDectector = new Class_InternetDectector(getApplicationContext());
        isInternetPresent = internetDectector.isConnectingToInternet();

        if (isInternetPresent)
        {
            Get_UserClusterAcademicEmployeeData();
        }else{
          //  alerts_dialog_offline();
           Toast.makeText(getApplicationContext(), "Connect to Internet", Toast.LENGTH_LONG).show();
        }

      /*  if (!str_flag.equals("1")) {
            if (SaveSharedPreference.getUserName(AdminHomeActivity.this).length() == 0) {
//                Intent i = new Intent(Activity_HomeScreen.this, NormalLogin.class);
//                startActivity(i);
//                finish();
//                // call Login Activity
            } else {
                str_Googlelogin_Username=SaveSharedPreference.getUserName(AdminHomeActivity.this);
                // Stay at the current activity.
            }

        } else {

            if (SaveSharedPreference.getUserName(AdminHomeActivity.this).length() == 0) {
                Intent i = new Intent(AdminHomeActivity.this, MainActivity.class);
                startActivity(i);
                finish();
                // call Login Activity
            } else {
                // Stay at the current activity.
            }
        }*/


        clusterList = new ArrayList<ClusterSummaryList>();

        clusterListViewAdapter = new AdminListViewAdapter(AdminClustersActivity.this, clusterList);

        summaryList =new ArrayList<ClusterSummaryList>();

        summaryListViewAdapter = new ClusterSummaryListViewAdapter(AdminClustersActivity.this, summaryList);

        yearlist_farmer_SPl.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Obj_Class_yearDetails = (ClusterAcademicList) yearlist_farmer_SPl.getSelectedItem();
                sp_stryear_ID = Obj_Class_yearDetails.getYearID().toString();
                selected_year = yearlist_farmer_SPl.getSelectedItem().toString();
                int sel_yearsp_new = yearlist_farmer_SPl.getSelectedItemPosition();

                if(isInternetPresent){
                    Get_UserClusterAcademicSummary();
                }else{
                    Toast.makeText(getApplicationContext(), "Connect to Internet", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        etSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Call back the Adapter with current character to Filter
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                //adapter.getFilter().filter(s.toString());
                String text = etSearch.getText().toString().toLowerCase(Locale.getDefault());
                clusterListViewAdapter.filter(text,originalList,null);
            }
        });
        clusterListViewAdapter = new AdminListViewAdapter(AdminClustersActivity.this, clusterList);
        lview.setAdapter(clusterListViewAdapter);

    }   // end onCreate

    private void GetAppVersionCheck(){
//        Map<String,String> params = new HashMap<String, String>();
//
//        params.put("User_ID","90");// for dynamic

        Call call = userService1.getAppVersion();
        final ProgressDialog progressDoalog;
        progressDoalog = new ProgressDialog(AdminClustersActivity.this);
        progressDoalog.setMessage("Loading....");
        progressDoalog.setTitle("Please wait....");
        progressDoalog.setCancelable(false);
        progressDoalog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        // show it
        progressDoalog.show();
        call.enqueue(new Callback<GetAppVersion>()
        {
            @Override
            public void onResponse(Call<GetAppVersion> call, Response<GetAppVersion> response)
            {
                Log.e("response",response.toString());
                Log.e("TAG", "response: "+new Gson().toJson(response) );
                Log.e("response body", String.valueOf(response.body()));

                if(response.isSuccessful())
                {
                    //        progressDoalog.dismiss();
                    GetAppVersion  class_loginresponse = response.body();
                    Log.e("tag","res=="+class_loginresponse.toString());
                    if(class_loginresponse.getStatus()) {


                        List<GetAppVersionList> getAppVersionList=response.body().getlistVersion();
                        String str_releaseVer=getAppVersionList.get(0).getAppVersion();

                        int int_versioncode= Integer.parseInt(versioncode);
                        int int_releaseVer= Integer.parseInt(str_releaseVer);

                        Log.e("tag","str_releaseVer="+str_releaseVer);
                        if(int_releaseVer>int_versioncode)
                        {
                            //call pop
                            Log.e("popup","popup");
                            //alerts();
                            alerts_dialog_playstoreupdate();
                        }
                        else{
                        }
                        progressDoalog.dismiss();
                    }else{
                        progressDoalog.dismiss();
                        Toast.makeText(AdminClustersActivity.this, class_loginresponse.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                } else {
                    progressDoalog.dismiss();

                    DefaultResponse error = ErrorUtils.parseError(response);
                    // … and use it to show error information

                    // … or just log the issue like we’re doing :)
                    Log.d("error message", error.getMsg());

                    Toast.makeText(AdminClustersActivity.this, error.getMsg(), Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(Call call, Throwable t)
            {
                progressDoalog.dismiss();
                Log.e("TAG", "onFailure: "+t.toString() );

                Log.e("tag","Error:"+t.getMessage());
                Toast.makeText(AdminClustersActivity.this, "WS:Error:"+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });



    }

    public  void alerts_dialog_playstoreupdate()
    {

        AlertDialog.Builder dialog = new AlertDialog.Builder(AdminClustersActivity.this);
        dialog.setCancelable(false);
        dialog.setTitle("DF Agri");
        dialog.setMessage("Kindly update from playstore");

        dialog.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id)
            {
                Intent	intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=df.farmponds"));
                startActivity(intent);
            }
        });


        final AlertDialog alert = dialog.create();
        alert.setOnShowListener( new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#004D40"));
            }
        });
        alert.show();

    }

    public  void alerts_dialog_offline()
    {

        AlertDialog.Builder dialog = new AlertDialog.Builder(AdminClustersActivity.this);
        dialog.setCancelable(false);
        dialog.setTitle("DF Agri");
        dialog.setMessage(" You are Offline\n\n Please Connect to Internet");

        dialog.setPositiveButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id)
            {
                Intent startMain = new Intent(Intent.ACTION_MAIN);
                startMain.addCategory(Intent.CATEGORY_HOME);
                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(startMain);
            }
        });


        final AlertDialog alert = dialog.create();
        alert.setOnShowListener( new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#004D40"));
            }
        });
        alert.show();

    }


    private void Get_UserClusterAcademicEmployeeData() {

        Call<ClusterAcademicEmployee> call = userService1.get_UserClusterAcademicEmployeeData(str_employee_id);

        final ProgressDialog progressDoalog;
        progressDoalog = new ProgressDialog(AdminClustersActivity.this);
        progressDoalog.setMessage("Loading....");
        progressDoalog.setTitle("Please wait....");
        progressDoalog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        // show it
        progressDoalog.show();

        call.enqueue(new Callback<ClusterAcademicEmployee>() {
            @Override
            public void onResponse(Call<ClusterAcademicEmployee> call, Response<ClusterAcademicEmployee> response) {
                //   Log.e("Entered resp", response.message());
                //     Log.e("Entered resp", response.body().getMessage());
                Log.e("TAG", "response userdata: " + new Gson().toJson(response));
                Log.e("response body userdata", String.valueOf(response.body()));

                if (response.isSuccessful()) {
                    ClusterAcademicEmployee class_userData = response.body();
                    //   Log.e("response.body", response.body().getLst().toString());
                    if (class_userData.getStatus().equals(true)) {
                        if(class_userData.getMessage().equalsIgnoreCase("Success")) {
                            List<ClusterList> yearlist = response.body().getLst();
                            Log.e("programlist.size()", String.valueOf(yearlist.size()));

                            cluster_dataLists = new ClusterAcademicEmployee[yearlist.size()];
                            //   Log.e("tag","Pond Id=="+class_userData.getLst().get(0).getPond().get(0).getPondID());

                            for (int i = 0; i < cluster_dataLists.length; i++) {

                                Log.e("status", String.valueOf(class_userData.getStatus()));

                                class_cluster_dataList.setAcademic((class_userData.getLst().get(i).getAcademic()));
                             //   class_cluster_dataList.setEmployee(class_userData.getLst().get(i).getEmployee());


                                int sizeAcademic = class_userData.getLst().get(i).getAcademic().size();
                                arrayObj_Class_yearDetails2 = new ClusterAcademicList[sizeAcademic];

                                for (int j = 0; j < sizeAcademic; j++) {
                                    Log.e("tag", "YearID ==" + class_userData.getLst().get(i).getAcademic().get(j).getYearID());

                                    Log.e("tag", "YearName==" + class_userData.getLst().get(i).getAcademic().get(j).getYearName());
                                    String YearID = class_userData.getLst().get(i).getAcademic().get(j).getYearID();
                                    String YearName = class_userData.getLst().get(i).getAcademic().get(j).getYearName();
                                    ClusterAcademicList innerObj_Class_yearList = new ClusterAcademicList();
                                    innerObj_Class_yearList.setYearID(class_userData.getLst().get(i).getAcademic().get(j).getYearID());
                                    innerObj_Class_yearList.setYearName(class_userData.getLst().get(i).getAcademic().get(j).getYearName());

                                    arrayObj_Class_yearDetails2[j] = innerObj_Class_yearList;

                                }
                                ArrayAdapter dataAdapter = new ArrayAdapter(getApplicationContext(), R.layout.spinnercenterstyle, arrayObj_Class_yearDetails2);
                                dataAdapter.setDropDownViewResource(R.layout.spinnercenterstyle);
                                yearlist_farmer_SPl.setAdapter(dataAdapter);
                            }

                            //  uploadfromDB_Farmerlist();
                        }
                        else{
                            Toast.makeText(AdminClustersActivity.this, class_userData.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        progressDoalog.dismiss();
                    } else {
                        progressDoalog.dismiss();

                        Toast.makeText(AdminClustersActivity.this, class_userData.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                } else {
                    progressDoalog.dismiss();

                    Log.e("Entered resp else", "");
                    DefaultResponse error = ErrorUtils.parseError(response);
                    // … and use it to show error information

                    // … or just log the issue like we’re doing :)
                    Log.e("error message", error.getMsg());

                    // Toast.makeText(Activity_ViewFarmers.this, error.getMsg(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Toast.makeText(AdminClustersActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });// end of call

    }

    private void Get_UserClusterAcademicSummary() {

        Call<ClusterAcademicSummary> call = userService1.get_UserClusterAcademicSummary(str_employee_id,sp_stryear_ID);

        final ProgressDialog progressDoalog;
        progressDoalog = new ProgressDialog(AdminClustersActivity.this);
        progressDoalog.setMessage("Loading....");
        progressDoalog.setTitle("Please wait....");
        progressDoalog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        // show it
        progressDoalog.show();

        call.enqueue(new Callback<ClusterAcademicSummary>() {
            @Override
            public void onResponse(Call<ClusterAcademicSummary> call, Response<ClusterAcademicSummary> response) {
                //   Log.e("Entered resp", response.message());
                //     Log.e("Entered resp", response.body().getMessage());
                Log.e("TAG", "response userdata: " + new Gson().toJson(response));
                Log.e("response body userdata", String.valueOf(response.body()));

                if (response.isSuccessful()) {

                    ClusterAcademicSummary class_userData = response.body();
                    //   Log.e("response.body", response.body().getLst().toString());
                    if (class_userData.getStatus().equals(true)) {
                        if(class_userData.getMessage().equalsIgnoreCase("Success")) {
                            List<ClusterSummaryList> yearlist = response.body().getLstSummary();
                            Log.e("getLstSummary.size()", String.valueOf(yearlist.size()));
                            clusterList.clear();
                            summaryList.clear();
                            cluster_summaryLists = new ClusterAcademicSummary[yearlist.size()];
                            //   Log.e("tag","Pond Id=="+class_userData.getLst().get(0).getPond().get(0).getPondID());
                            arraysummarylist = new ClusterSummaryList[yearlist.size()];
                            for (int i = 0; i < cluster_summaryLists.length; i++) {

                                Log.e("status", String.valueOf(class_userData.getStatus()));


                                    class_cluster_summaryList.setDataAll((class_userData.getLstSummary().get(i).getDataAll()));
                                    class_cluster_summaryList.setDataApproved((class_userData.getLstSummary().get(i).getDataApproved()));
                                    class_cluster_summaryList.setDataPending((class_userData.getLstSummary().get(i).getDataPending()));
                                    class_cluster_summaryList.setDataProcess((class_userData.getLstSummary().get(i).getDataProcess()));
                                    class_cluster_summaryList.setDataRejected((class_userData.getLstSummary().get(i).getDataRejected()));
                                    class_cluster_summaryList.setEmployeeName((class_userData.getLstSummary().get(i).getEmployeeName()));
                                    class_cluster_summaryList.setUserID((class_userData.getLstSummary().get(i).getUserID()));

                                    arraysummarylist[i] = class_cluster_summaryList;
                                    // Log.e("arraysummarylist", arraysummarylist[i].getEmployeeName());
                                    //Log.e("getEmployeeProgram", arraysummarylist[i].getDataAll());
                                if(arraysummarylist[i].getEmployeeName().equalsIgnoreCase("All")){

                                    ClusterSummaryList item1 = null;
                                    item1 = new ClusterSummaryList("Summary", arraysummarylist[i].getDataAll(), arraysummarylist[i].getDataPending(), arraysummarylist[i].getDataProcess(), arraysummarylist[i].getDataApproved(), arraysummarylist[i].getDataRejected(), sp_stryear_ID, arraysummarylist[i].getUserID(),str_employee_id,"Cluster");

                                    summaryList.add(item1);
                                }else {
                                    ClusterSummaryList item = null;
                                    item = new ClusterSummaryList(arraysummarylist[i].getEmployeeName(), arraysummarylist[i].getDataAll(), arraysummarylist[i].getDataPending(), arraysummarylist[i].getDataProcess(), arraysummarylist[i].getDataApproved(), arraysummarylist[i].getDataRejected(), sp_stryear_ID, arraysummarylist[i].getUserID(),str_employee_id,"Cluster");

                                    clusterList.add(item);
                                }
                            }
                            summaryListViewAdapter = new ClusterSummaryListViewAdapter(AdminClustersActivity.this, summaryList);
                            listview_summarylist.setAdapter(summaryListViewAdapter);

                            originalList = new ArrayList<ClusterSummaryList>();
                            originalList.clear();
                            originalList.addAll(clusterList);

                            clusterListViewAdapter.notifyDataSetChanged();
                            //  uploadfromDB_Farmerlist();
                        }
                        else{
                            Toast.makeText(AdminClustersActivity.this, class_userData.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        progressDoalog.dismiss();
                    } else {
                        progressDoalog.dismiss();

                        Toast.makeText(AdminClustersActivity.this, class_userData.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                } else {
                    progressDoalog.dismiss();

                    Log.e("Entered resp else", "");
                    DefaultResponse error = ErrorUtils.parseError(response);
                    // … and use it to show error information

                    // … or just log the issue like we’re doing :)
                    Log.e("error message", error.getMsg());

                    // Toast.makeText(Activity_ViewFarmers.this, error.getMsg(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Toast.makeText(AdminClustersActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });// end of call

    }
    @Override
    public void onBackPressed() {
        Intent i = new Intent(AdminClustersActivity.this, ClusterHomeActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.logout_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        if(id==R.id.contactus)
        {

            Intent i = new Intent(AdminClustersActivity.this, ContactUs_Activity.class);
            startActivity(i);
            finish();
            return true;


        }




        if (id == R.id.logout)
        {
            // Toast.makeText(CalenderActivity.this, "Action clicked", Toast.LENGTH_LONG).show();
            internetDectector = new Class_InternetDectector(getApplicationContext());
            isInternetPresent = internetDectector.isConnectingToInternet();

            if (isInternetPresent)
            {

//                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//                SharedPreferences.Editor editor = settings.edit();
//                editor.remove("login_userEmail");
                SaveSharedPreference.setUserName(AdminClustersActivity.this, "");

                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                i.putExtra("Key_Logout", "yes");
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                finish();

//                Intent i = new Intent(getApplicationContext(), NormalLogin.class);
//                i.putExtra("logout_key1", "yes");
//                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                startActivity(i);

                //}
            } else {
                Toast.makeText(getApplicationContext(), "No Internet", Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        if (id == android.R.id.home)
        {
            //  Toast.makeText(getApplicationContext(),"Back button clicked", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(AdminClustersActivity.this, ClusterHomeActivity.class);
            startActivity(i);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}