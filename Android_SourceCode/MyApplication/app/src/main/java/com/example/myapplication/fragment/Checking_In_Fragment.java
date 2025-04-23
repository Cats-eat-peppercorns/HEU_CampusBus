package com.example.myapplication.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.R;
import com.example.myapplication.javabean.ServerSetting;
import com.example.myapplication.javabean.django_url;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Checking_In_Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Checking_In_Fragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private View rootView;
    private MaterialCalendarView mvc;
    private ImageButton clockIn_button;
    private List<CalendarDay> selectedDates_true = new ArrayList<>();

    private SharedPreferences sharedPreferences;

    private CalendarDay currentDate=CalendarDay.today();
    private TextView clockIn_days,lack_days;

    public Checking_In_Fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Checking_In_Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Checking_In_Fragment newInstance(String param1, String param2) {
        Checking_In_Fragment fragment = new Checking_In_Fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if(rootView==null)
        {
            rootView=inflater.inflate(R.layout.fragment_checking__in_, container, false);
        }
        mvc=rootView.findViewById(R.id.calendarView);
        clockIn_button=rootView.findViewById(R.id.clockIn_button);
        clockIn_days = rootView.findViewById(R.id.clockIn_days);
        lack_days=rootView.findViewById(R.id.lack_days);
        sharedPreferences= requireContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        getSignedDays();  //从服务器获取已签到日期
        set_calender();  //设置日历
        return rootView;
    }
    public void getSignedDays() {
        String url = ServerSetting.ServerPublicIpAndPort+"driver_getSignedDays/";
        String driver_account=sharedPreferences.getString("driver_account","");
        OkHttpClient okHttpClient = new OkHttpClient();  //构建一个网络类型的实例
        RequestBody requestBody = new FormBody.Builder()
                .add("driver_account", driver_account)
                .build();
        Request request = new Request.Builder() //构建一个具体的网络请求对象，具体的请求url，请求头，请求体等
                .url(url)
                .post(requestBody)
                .build();
        Call call = okHttpClient.newCall(request); //将具体的网络请求与执行请求的实体进行绑定
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(requireActivity(), "请检查网络", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull okhttp3.Response response) throws IOException {
                if (!response.isSuccessful()) {
                    // 请求不成功，处理错误响应
                    final String errorMessage = response.body().string();
                    // 切换到主线程更新 UI
                    requireActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(requireActivity(), "签到失败：" + errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    int year,month,day;
                    List<CalendarDay> selectedDates = new ArrayList<>();
                    try {
                        String responseData = response.body().string();
                        JSONArray jsonArray =new JSONArray(responseData);   //将string类型的response转换为JSONObject类型的object

                        for(int i=0;i<jsonArray.length();i++)
                        {
                            JSONObject object = jsonArray.getJSONObject(i);
                            year=Integer.parseInt(object.getString("year"));
                            month=Integer.parseInt(object.getString("month"));
                            day=Integer.parseInt(object.getString("day"));

                            selectedDates.add(CalendarDay.from(year,month-1,day));
                        }
                        //Log.e("TAG?!?!?!?!", "run: "+selectedDates);
                        selectedDates_true=selectedDates;
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    for (CalendarDay selectedDate : selectedDates) {
                        requireActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mvc.addDecorators(new SelectedDayDecorator(selectedDate, Color.parseColor("#ADD8E6")));
                            }
                        });
                        if(selectedDate.equals(currentDate)){
                            requireActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    clockIn_button.setImageResource(R.drawable.clock_in_ok);
                                }
                            });
                        }
                    }
                    //
                    int month_work_days=0;
                    int month_lack_days;
                    for (CalendarDay selectedDate : selectedDates) {
                        if(selectedDate.getMonth()+1==currentDate.getMonth()+1&&selectedDate.getYear()==currentDate.getYear())
                        {
                            month_work_days++;
                        }
                    }
                    month_lack_days=currentDate.getDay()-month_work_days;
                    int finalMonth_work_days = month_work_days;
                    requireActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            lack_days.setText(String.valueOf(month_lack_days));
                            clockIn_days.setText(String.valueOf(finalMonth_work_days));
                        }
                    });
                }
            }
        });
    }
    private void set_calender() {
        mvc.setSelectionMode(MaterialCalendarView.SELECTION_MODE_NONE);
        Calendar minDate = Calendar.getInstance();
        minDate.add(Calendar.YEAR, -1);
        Calendar maxDate = Calendar.getInstance();
        maxDate.add(Calendar.YEAR, 1);
        mvc.state().edit()
                .setMinimumDate(minDate)
                .setMaximumDate(maxDate)
                .commit();

        //将今天变为选中状态
        mvc.setDateSelected(currentDate, true);

        // 将今天及以前的所有日期标记为红色
        CalendarDay startDay = currentDate;
        while (startDay.isAfter(CalendarDay.from(currentDate.getYear(), currentDate.getMonth(), 1))) {
            mvc.addDecorators(new DayDecorator(startDay, Color.RED));
            Calendar calendar = startDay.getCalendar();
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            startDay = CalendarDay.from(calendar);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        clockIn_button.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View view) {
                Drawable background = clockIn_button.getDrawable();
                Drawable grayDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.clock_in_none);
                if (background==null||background.getConstantState() == null || background.getConstantState().equals(grayDrawable.getConstantState())) {
                    // 若是空或为灰色则可执行签到

                    //Log.e("TAG?!?!?!?!", "run: ？？？？");
                    send_clockIn();  // 向服务器发送签到数据
                }
            }
        });
        mvc.setOnMonthChangedListener(new OnMonthChangedListener() {  //翻页监听器
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
                int month = date.getMonth() + 1; // 月份是从0开始计数的，所以要加1
                int year = date.getYear();
                Calendar calendar=date.getCalendar();
                int total_days=calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                int month_work_days=0;
                int month_lack_days;
                for (CalendarDay selectedDate : selectedDates_true) {
                    if(selectedDate.getMonth()+1==month&&selectedDate.getYear()==year)
                    {
                        month_work_days++;
                    }
                }
                if(year==currentDate.getYear()&&month==currentDate.getMonth()+1)
                    month_lack_days=currentDate.getDay()-month_work_days;
                else
                    month_lack_days=total_days-month_work_days;
                lack_days.setText(String.valueOf(month_lack_days));
                clockIn_days.setText(String.valueOf(month_work_days));
            }
        });
    }

    public void send_clockIn() {
        String url = ServerSetting.ServerPublicIpAndPort+"driver_send_clockIn_date/";
        String driver_account=sharedPreferences.getString("driver_account","");
        OkHttpClient okHttpClient = new OkHttpClient();  //构建一个网络类型的实例
        RequestBody requestBody = new FormBody.Builder()
                .add("driver_account", driver_account)
                .build();
        Request request = new Request.Builder() //构建一个具体的网络请求对象，具体的请求url，请求头，请求体等
                .url(url)
                .post(requestBody)
                .build();
        Call call = okHttpClient.newCall(request); //将具体的网络请求与执行请求的实体进行绑定
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(requireActivity(), "签到失败：请检查网络", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull okhttp3.Response response) throws IOException {
                if (!response.isSuccessful()) {
                    // 请求不成功，处理错误响应
                    final String errorMessage = response.body().string();
                    // 切换到主线程更新 UI
                    requireActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(requireActivity(), "签到失败：" + errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    requireActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(), "签到成功", Toast.LENGTH_SHORT).show();
                            getSignedDays(); //重新获取一次签到数据
                        }
                    });

                }

            }
        });
    }
}

// DayDecorator 类用于标记今天以前的日期为红色
class DayDecorator implements DayViewDecorator {
    private final CalendarDay date;
    private final int color;

    public DayDecorator(CalendarDay date, int color) {
        this.date = date;
        this.color = color;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {  //判断是否应该装饰该日期
        return day.isBefore(date) || day.equals(date);
    }

    @Override
    public void decorate(DayViewFacade view) { //实际装饰操作
        view.addSpan(new ForegroundColorSpan(color));
    }
}
// SelectedDayDecorator 类用于给定日期添加浅蓝色圆圈
class SelectedDayDecorator implements DayViewDecorator {
    private final CalendarDay date;
    private final int color;

    public SelectedDayDecorator(CalendarDay date, int color) {
        this.date = date;
        this.color = color;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return day.equals(date);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.setSelectionDrawable(new ColorDrawable(color));
        view.addSpan(new ForegroundColorSpan(Color.WHITE));
    }
}