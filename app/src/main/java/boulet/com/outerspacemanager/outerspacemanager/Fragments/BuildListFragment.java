package boulet.com.outerspacemanager.outerspacemanager.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import boulet.com.outerspacemanager.outerspacemanager.Activity.BuildingActivity;
import boulet.com.outerspacemanager.outerspacemanager.R;

/**
 * Created by aboulet on 05/03/2018.
 */

public class BuildListFragment extends Fragment {
    private ListView lvFragList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_build_list,container);
        lvFragList = v.findViewById(R.id.lvFragList);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        //lvFragList.setAdapter(new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, listItems));
        lvFragList.setOnItemClickListener((BuildingActivity)getActivity());
    }
}
