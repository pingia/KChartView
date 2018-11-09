package com.github.tifezh.kchart.chart;

import com.github.tifezh.kchart.DataHelper;
import com.github.tifezh.kchartlib.chart.BaseKChartAdapter;
import com.github.tifezh.kchartlib.utils.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 数据适配器
 * Created by tifezh on 2016/6/18.
 */

public class KChartAdapter<T extends KLineEntity> extends BaseKChartAdapter {

    private List<T> mDataList = new ArrayList<>();

    public List<T> getDataList() {
        return mDataList;
    }

    @Override
    public int getCount() {
        return mDataList.size();
    }

    @Override
    public T getItem(int position) {
        return mDataList.get(position);
    }

    @Override
    public Date getDate(int position) {
        return new Date(mDataList.get(position).getTimeStamp());
    }

    public void addFooterData(List<T> list){
        if(!CollectionUtils.isEmpty(list)) {
            mDataList.addAll(list);
            DataHelper.calculate(mDataList);
            notifyDataSetChanged();
        }
    }

    public void addHeaderData(List<T> list){
        if(!CollectionUtils.isEmpty(list)) {
            mDataList.addAll(0, list);
            DataHelper.calculate(mDataList);
            notifyDataSetChanged();
        }
    }

    public void setData(List<T> list){
        if(!CollectionUtils.isEmpty(list)) {
            mDataList.clear();
            mDataList.addAll(list);
            DataHelper.calculate(list);
            notifyDataSetChanged();
        }
    }

}
