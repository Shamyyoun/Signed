package adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mahmoudelshamy.signed.R;

import datamodels.Log;

/**
 * Created by Shamyyoun on 2/8/2015.
 */
public class LogsAdapter extends RecyclerView.Adapter<LogsAdapter.ViewHolder> {
    private Context context;
    private Log[] data;
    private int layoutResourceId;

    public LogsAdapter(Context context, Log[] data, int layoutResourceId) {
        this.context = context;
        this.data = data;
        this.layoutResourceId = layoutResourceId;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Log log = data[position];

        // set data
        holder.textDay.setText(log.getDay() + ",\n" + log.getDayName());
        holder.textLoginTime.setText(log.getLoginTime().isEmpty() ? "--" : log.getLoginTime());
        holder.textLogoutTime.setText(log.getLogoutTime().isEmpty() ? "--" : log.getLogoutTime());

        // customize background
        if (log.isWeekend())
            holder.layoutRoot.setBackgroundColor(context.getResources().getColor(R.color.weekend));
        else if (log.isHoliday())
            holder.layoutRoot.setBackgroundColor(context.getResources().getColor(R.color.holiday));
        else if (log.isMissed())
            holder.layoutRoot.setBackgroundColor(context.getResources().getColor(R.color.missed));
        else if (log.isComplete())
            holder.layoutRoot.setBackgroundColor(context.getResources().getColor(R.color.complete));
        else if (!log.isComplete())
            holder.layoutRoot.setBackgroundColor(context.getResources().getColor(R.color.non_complete));
    }

    @Override
    public int getItemCount() {
        return data.length;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                layoutResourceId, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public View layoutRoot;
        public TextView textDay;
        public TextView textLoginTime;
        public TextView textLogoutTime;

        public ViewHolder(View v) {
            super(v);
            layoutRoot = v;
            textDay = (TextView) v.findViewById(R.id.text_day);
            textLoginTime = (TextView) v.findViewById(R.id.text_loginTime);
            textLogoutTime = (TextView) v.findViewById(R.id.text_logoutTime);
        }
    }

}