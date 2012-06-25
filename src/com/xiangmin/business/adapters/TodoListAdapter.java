package com.xiangmin.business.adapters;

import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xiangmin.business.R;
import com.xiangmin.business.models.Todo;

public class TodoListAdapter extends BaseAdapter {

		private LayoutInflater inflater;
		private List<Todo> list;

		public TodoListAdapter(LayoutInflater inflater, List<Todo> list) {
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
			if (position < list.size()) {
				return list.get(position).id;
			} else
				return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Todo todo = list.get(position);
			View view;
			if (convertView != null	&& convertView.getId() == R.id.todolist_item) {
				view = convertView;
			} else {
				view = inflater.inflate(R.layout.todolist_item, parent, false);
			}

			TodoFolderViewHolder holder = (TodoFolderViewHolder) view.getTag();
			if (holder == null) {
				holder = new TodoFolderViewHolder();
				holder.clientNameView = (TextView) view.findViewById(R.id.client_name_view);
				holder.clientTodoTime = (TextView) view.findViewById(R.id.client_todo_time);
				holder.todoState = (TextView) view.findViewById(R.id.todo_state);
				holder.clientAddress = (TextView) view.findViewById(R.id.client_address);
				view.setTag(holder);
			}

			if (todo != null) {
				holder.clientNameView.setText(todo.clientName);
				holder.clientTodoTime.setText(todo.todoTime);
				holder.clientAddress.setText(todo.address);
				holder.todoState.setText(todo.state);
				view.setTag(holder);
			} else {
				holder.clientNameView.setText("error");
			}
			return view;
		}

	}

	class TodoFolderViewHolder {
		public TextView clientNameView;
		public TextView clientTodoTime;
		public TextView todoState;
		public TextView clientAddress;
	}
