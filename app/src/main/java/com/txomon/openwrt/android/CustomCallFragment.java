package com.txomon.openwrt.android;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.txomon.openwrt.ubusrpc.UbusRpcException;
import com.txomon.rx.Events;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
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

        final Observable<Object> sendCallClick = Events.click(sendButton);

        sendCallClick
                .observeOn(Schedulers.io())
                .flatMap(new Func1<Object, Observable<Object>>() {
                    @Override
                    public Observable<Object> call(Object o) {
                        try {
                            UbusRpcFragmentInteractionListenerInterface activity = (UbusRpcFragmentInteractionListenerInterface) main;
                            return Observable.just(
                                    activity.makeUbusRpcClientCall(
                                            ubusObject.getText().toString(),
                                            ubusMethod.getText().toString(),
                                            null
                                    )
                            );
                        } catch (Throwable e) {
                            return Observable.error(e);
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        if (throwable instanceof UbusRpcException) {
                            UbusRpcFragmentInteractionListenerInterface activity = (UbusRpcFragmentInteractionListenerInterface) main;
                            activity.handleCallError(throwable.getMessage());
                        }
                    }
                })
                .retry(new Func2<Integer, Throwable, Boolean>() {
                    @Override
                    public Boolean call(Integer integer, Throwable throwable) {
                        return throwable instanceof UbusRpcException;
                    }
                })
                .subscribe(((UbusRpcFragmentInteractionListenerInterface) main).getCallResultObserver());
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            UbusRpcFragmentInteractionListenerInterface mListener = (UbusRpcFragmentInteractionListenerInterface) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement UbusRpcFragmentInteractionListenerInterface");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


}
