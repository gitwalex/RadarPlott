package de.alexanderwinkler.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.alexanderwinkler.R;

import static android.support.v7.widget.RecyclerView.Adapter;

/**
 * Created by alex on 05.08.2016.
 */
public class FragmentEingabeDaten extends Fragment {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_eingabedaten, container, false);
        RecyclerView rv = (RecyclerView) view.findViewById(R.id.rvEingabeDaten);
        rv.setAdapter(new EingabeDatenAdapter());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(layoutManager);
        return view;
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    private class EingabeDatenAdapter extends Adapter {
        private static final int VIEWTYPE_EIGENES_SCHIFF = 0;
        private static final int VIEWTYPE_GEGNER = 1;
        private int itemCount = 4;

        @Override
        public int getItemCount() {
            return itemCount;
        }

        @Override
        public int getItemViewType(int position) {
            switch (position) {
                case 0:
                    return VIEWTYPE_EIGENES_SCHIFF;
                default:
                    return VIEWTYPE_GEGNER;
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            switch (holder.getItemViewType()) {
                case VIEWTYPE_GEGNER:
                    String text = String.valueOf((char) ('A' + position));
                    TextView tv = (TextView) holder.itemView.findViewById(R.id.HeaderGegner);
                    String s = String.format(getResources().getString(R.string.Gegner), text);
                    tv.setText(s);
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v;
            switch (viewType) {
                case VIEWTYPE_EIGENES_SCHIFF:
                    v = LayoutInflater.from(getActivity())
                            .inflate(R.layout.view_daten_eigenes_schiff, parent, false);
                    break;
                default:
                    v = LayoutInflater.from(getActivity())
                            .inflate(R.layout.view_daten_gegner, parent, false);
            }
            ViewHolder holder = new ViewHolder(v);
            return holder;
        }
    }
}
