package space.liberion.meetandsports.ticket;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import space.liberion.meetandsports.R;
import space.liberion.meetandsports.main.BaseFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class TicketFragment extends BaseFragment {


    public TicketFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ticket, container, false);
    }

}
