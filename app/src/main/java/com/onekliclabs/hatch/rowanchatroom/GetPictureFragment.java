package com.onekliclabs.hatch.rowanchatroom;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * A simple {Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {GetPictureFragment.OnFragmentInteractionListener} interface
 */
public class GetPictureFragment extends Fragment
{
    public static final int CAMERA_BUTTON_POSITION = 1;
    public static final int LIBRARY_BUTTON_POSITION = 2;

    private OnFragmentInteractionListener mListener;


    public GetPictureFragment()
    {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_getpicture, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        //initialize fragment widgets
        Button library = (Button) getView().findViewById(R.id.button_library);
        Button camera = (Button) getView().findViewById(R.id.button_camera);

        camera.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onButtonPressed(CAMERA_BUTTON_POSITION);
            }
        });

        library.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onButtonPressed(LIBRARY_BUTTON_POSITION);
            }
        });


    }

    public void onButtonPressed(int position)
    {
        if (mListener != null)
        {
            mListener.onFragmentInteraction(position);
        }
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e)
        {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener
    {
        void onFragmentInteraction(int position);
    }

}
