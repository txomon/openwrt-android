package com.txomon.openwrt.android;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.txomon.openwrt.rpc.UbusRpcException;
import com.txomon.rx.Events;

import java.util.Map;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


public class CustomCallFragment extends Fragment {
    private static final String TAG = "CustomCallFragment";


    public CustomCallFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final Activity main = getActivity();
        final View view = inflater.inflate(R.layout.fragment_custom_call, container, false);
        final EditText ubusMethod = (EditText) view.findViewById(R.id.customCallFragmentMethodText);
        final EditText ubusObject = (EditText) view.findViewById(R.id.customCallFragmentPathText);
        final Button sendButton = (Button) view.findViewById(R.id.customCallFragmentSendButton);

        final Observable<String> ubusObjectText = Events.text(ubusObject);
        final Observable<String> ubusMethodText = Events.text(ubusMethod);
        final Observable<Object> sendCallClick = Events.click(sendButton);

        //   .subscribeOn(AndroidSchedulers.mainThread())
        //   .observeOn(Schedulers.io())
        sendCallClick
                .observeOn(Schedulers.io())
                .flatMap(new Func1<Object, Observable<Object>>() {
                    @Override
                    public Observable<Object> call(Object o) {
                        return Observable.create(
                                new Observable.OnSubscribe<Object>() {
                                    @Override
                                    public void call(Subscriber<? super Object> subscriber) {
                                        OnCustomCallFragmentInteractionListener activity = (OnCustomCallFragmentInteractionListener) main;
                                        try {
                                            subscriber.onNext(
                                                    activity.makeUbusRpcClientCall(
                                                            ubusObject.getText().toString(),
                                                            ubusMethod.getText().toString(),
                                                            null));
                                        } catch (Throwable e) {
                                            Log.d(TAG,"Catching throwable in Observable" );
                                            subscriber.onError(e);
                                        }
                                        subscriber.onCompleted();
                                    }
                                }
                        );

                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(((OnCustomCallFragmentInteractionListener) main).getCallResultObserver());
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            OnCustomCallFragmentInteractionListener mListener = (OnCustomCallFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnCustomCallFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public interface OnCustomCallFragmentInteractionListener {
        public Object makeUbusRpcClientCall(String method, String path, Map arguments) throws UbusRpcException;

        public Observer<Object> getCallResultObserver();
    }

}
