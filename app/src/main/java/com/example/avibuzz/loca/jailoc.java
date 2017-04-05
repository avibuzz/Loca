package com.example.avibuzz.loca;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class jailoc extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Toast.makeText(getContext(),"jaigaon is set to your home location",Toast.LENGTH_SHORT).show();





        Intent i=new Intent(getActivity(),MainActivity.class);
        i.putExtra("latitude","26.51");
        i.putExtra("longitude","89.23");
        i.putExtra("home","jaigaon");
        getActivity().startActivity(i);










        return inflater.inflate(R.layout.fragment_jailoc, container, false);







    }



}
