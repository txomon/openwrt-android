package com.txomon.openwrt.android;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.txomon.rx.Events;

import rx.Observable;

public class ObjectExploreFragment extends Fragment {

    public ObjectExploreFragment() {
        // Required empty public constructor
    }

    public static ObjectExploreFragment newInstance(String param1, String param2) {
        ObjectExploreFragment fragment = new ObjectExploreFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final Activity main = getActivity();
        final View view = inflater.inflate(R.layout.fragment_explore, container, false);
        final ExpandableListView objectList = (ExpandableListView) view.findViewById(R.id.objectListView);

        final Observable<Integer> objectListClick = Events.itemClick(objectList);


        return view;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof UbusRpcFragmentInteractionListenerInterface))
            throw new ClassCastException(activity.toString()
                    + " must implement UbusRpcFragmentInteractionListenerInterface");

        if (!(activity instanceof ObjectExploreFragmentInteractionListenerInterface))
            throw new ClassCastException(activity.toString()
                    + " must implement ObjectExploreFragmentInteractionListenerInterface");
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public interface ObjectExploreFragmentInteractionListenerInterface {
        void switchToMethodExploreFragment(String object);
    }


}
