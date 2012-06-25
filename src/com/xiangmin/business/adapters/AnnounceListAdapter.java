package com.xiangmin.business.adapters;

import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xiangmin.business.R;
import com.xiangmin.business.models.Announce;

public class AnnounceListAdapter extends BaseAdapter {

		private LayoutInflater inflater;
		private List<Announce> list;

		public AnnounceListAdapter(LayoutInflater inflater, List<Announce> list) {
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
			Announce announce = list.get(position);
			View view;
			if (convertView != null	&& convertView.getId() == R.id.announcelist_item) {
				view = convertView;
			} else {
				view = inflater.inflate(R.layout.announcelist_item, parent, false);
			}

			AnnounceFolderViewHolder holder = (AnnounceFolderViewHolder) view.getTag();
			if (holder == null) {
				holder = new AnnounceFolderViewHolder();
				holder.announceType = (TextView) view.findViewById(R.id.announce_type);
				holder.announceTitle = (TextView) view.findViewById(R.id.announce_title);
				view.setTag(holder);
			}

			if (announce != null) {
				holder.announceType.setText(announce.announceType);
				holder.announceTitle.setText(announce.announceTitle);
				view.setTag(holder);
			} else {
				holder.announceType.setText("error");
			}
			return view;
		}

	}

	class AnnounceFolderViewHolder {
		public TextView announceType;
		public TextView announceTitle;
	}
