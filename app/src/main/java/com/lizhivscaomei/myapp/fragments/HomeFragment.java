package com.lizhivscaomei.myapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lizhivscaomei.myapp.MyApplication;
import com.lizhivscaomei.myapp.R;
import com.lizhivscaomei.myapp.module.entity.LedgerEntity;
import com.lizhivscaomei.myapp.module.view.LedgerMainActivity;

import org.xutils.ex.DbException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private TextView totalAmountYear, totalWeightYear;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
//        WebView webView=(WebView)inflater.inflate(R.id.homeWebView,container);
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        LinearLayout appLayout = (LinearLayout) view.findViewById(R.id.app_ledger);
        appLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), LedgerMainActivity.class));
            }
        });
        totalAmountYear = (TextView) view.findViewById(R.id.total_amount_year);
        totalWeightYear = (TextView) view.findViewById(R.id.total_weight_year);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getTotalYear();
    }

    public void getTotalYear() {
        new AsyncTask<Void, Void, Map<String,Float>>() {

            @Override
            protected Map<String,Float> doInBackground(Void... voids) {
                try {
                    SimpleDateFormat sf=new SimpleDateFormat("yyyy");
                    Cursor cursor = MyApplication.getXDbManager().execQuery("select sum(total_amount) total_amount_year,sum(total_weight) total_weight_year from ledger where date like '%" + sf.format(new Date()) + "%'");
                    if(cursor!=null&&cursor.moveToFirst()){
                        Map<String,Float> result=new HashMap<>();
                        result.put("total_amount_year",cursor.getFloat(cursor.getColumnIndex("total_amount_year")));
                        result.put("total_weight_year",cursor.getFloat(cursor.getColumnIndex("total_weight_year")));
                        return result;
                    }
                } catch (DbException e) {
                }
                return null;
            }

            @Override
            protected void onPostExecute(Map<String, Float> stringFloatMap) {
                super.onPostExecute(stringFloatMap);
                if(stringFloatMap!=null){
                    if(stringFloatMap.get("total_amount_year")!=null&&!stringFloatMap.get("total_amount_year").isNaN()&&stringFloatMap.get("total_amount_year").floatValue()>0){
                        totalAmountYear.setText(String.format("%.2f",stringFloatMap.get("total_amount_year").floatValue()));
                    }
                    if(stringFloatMap.get("total_weight_year")!=null&&!stringFloatMap.get("total_weight_year").isNaN()&&stringFloatMap.get("total_weight_year").floatValue()>0){
                        totalWeightYear.setText(String.format("%.2f",stringFloatMap.get("total_weight_year").floatValue()));
                    }
                }else {
                    totalAmountYear.setText("0.00");
                    totalWeightYear.setText("0.00");
                }
            }
        }.execute();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
