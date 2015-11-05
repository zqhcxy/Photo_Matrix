package com.example.photo_matrix;

import java.util.ArrayList;

import com.example.photo_matrix.MainActivity.OnItemClickLitener;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

public class RecyclerAdapter extends
		RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
	private ArrayList<String> menulist;
	OnItemClickLitener mOnItemClickLitener;

	public RecyclerAdapter(ArrayList<String> menulist) {
		this.menulist = menulist;
	}

	public void setOnItemOnClickLitener(OnItemClickLitener mOnItemClickLitener) {
		this.mOnItemClickLitener = mOnItemClickLitener;
	}

	@Override
	public int getItemCount() {
		// TODO Auto-generated method stub
		return menulist.size();
	}

	@Override
	public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
		// 绑定数据到ViewHolder上
		viewHolder.mTextView.setText(menulist.get(position));
		viewHolder.mTextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// int pos = viewHolder.getLayoutPosition();
				mOnItemClickLitener.onItemClick(viewHolder.mTextView, position);
			}
		});

	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
		// 创建一个View，简单起见直接使用系统提供的布局，就是一个TextView
		View view = View.inflate(viewGroup.getContext(),
				R.layout.matrix_menu_item, null);
		// 创建一个ViewHolder
		ViewHolder holder = new ViewHolder(view);
		return holder;
	}

	public static class ViewHolder extends RecyclerView.ViewHolder {

		public TextView mTextView;

		public ViewHolder(View itemView) {
			super(itemView);
			mTextView = (TextView) itemView.findViewById(R.id.menu_title);
		}
	}
}
