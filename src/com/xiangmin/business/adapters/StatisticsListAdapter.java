package com.xiangmin.business.adapters;

import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xiangmin.business.R;
import com.xiangmin.business.models.Statistics;

public class StatisticsListAdapter extends BaseAdapter {

		private LayoutInflater inflater;
		private List<Statistics> list;

		public StatisticsListAdapter(LayoutInflater inflater, List<Statistics> list) {
			super();
			this.inflater = inflater;
			this.list = list;
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			if (position < list.size()) {
				return list.get(position);
			} else
				return null;
		}

		@Override
		public long getItemId(int position) {
				return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Statistics statistics = list.get(position);
			View view;
			if (convertView != null	&& convertView.getId() == R.id.statisticslist_item) {
				view = convertView;
			} else {
				view = inflater.inflate(R.layout.statisticslist_item, parent, false);
			}

			StatisticsViewHolder holder = (StatisticsViewHolder) view.getTag();
			if (holder == null) {
				holder = new StatisticsViewHolder();
				holder.data = (TextView) view.findViewById(R.id.statistics_data);
				holder.warrantyPeriodCount = (TextView) view.findViewById(R.id.statistics_periodcount);
				holder.warrantyExpiredCount = (TextView) view.findViewById(R.id.statistics_expiredcount);
				holder.unfinishedCount = (TextView) view.findViewById(R.id.statistics_unfinished);
				holder.finishedCount = (TextView) view.findViewById(R.id.statistics_finished);
				holder.warrantyPeriodClearingMoney = (TextView) view.findViewById(R.id.statistics_periodclearingmoney);
				holder.warrantyExpiredClearingMoney = (TextView) view.findViewById(R.id.statistics_expiredclearingmoney);
				
				view.setTag(holder);
			}

			if (statistics != null) {
				holder.data.setText(statistics.data);
				holder.warrantyPeriodCount.setText(statistics.warrantyPeriodCount);
				holder.warrantyExpiredCount.setText(statistics.warrantyExpiredCount);
				holder.unfinishedCount.setText(statistics.unfinishedCount);
				holder.finishedCount.setText(statistics.finishedCount);
				holder.warrantyPeriodClearingMoney.setText(statistics.warrantyPeriodClearingMoney);
				holder.warrantyExpiredClearingMoney.setText(statistics.warrantyExpiredClearingMoney);
				view.setTag(holder);
			} else {
				holder.data.setText("error");
			}
			return view;
		}

	}

	class StatisticsViewHolder {
		public TextView data;
		public TextView warrantyPeriodCount;
		public TextView warrantyExpiredCount;
		public TextView unfinishedCount;
		public TextView finishedCount;
		public TextView warrantyPeriodClearingMoney;
		public TextView warrantyExpiredClearingMoney;
		
	}
