package com.github.tifezh.kchartlib.chart.formatter;

import com.github.tifezh.kchartlib.chart.base.IValueFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 对较大数据进行格式化
 * Created by tifezh on 2017/12/13.
 */

public class BigValueFormatter implements IValueFormatter {

    //必须是排好序的
    private List<Integer> unitValues = new ArrayList<>();
    private List<String> unitStrs = new ArrayList<>();
    private List<Integer> decimalNumbers = new ArrayList<>();
    private int mDefaultDecimalNumber;

    public BigValueFormatter(List<QuantityUnit> quantityUnits, int defaultDecimalNumber){
        if(null == quantityUnits || quantityUnits.size() ==0 ){
            return;
        }

        mDefaultDecimalNumber = defaultDecimalNumber;
        for (QuantityUnit unit : quantityUnits){
            unitValues.add(unit.getUnitValue());
            unitStrs.add(unit.getUnitStr());
            decimalNumbers.add(unit.getDecimalNumber());
        }

    }

    @Override
    public String format(double value) {
        int index = -1;
        int i=unitValues.size()-1;
        while (i>=0)
        {
            if(value>=unitValues.get(i)) {
                value /= unitValues.get(i);
                index = i;
                break;
            }
            i--;
        }

        if(index < 0 ){
            return String.format(Locale.getDefault(),"%." + mDefaultDecimalNumber + "f", value) ;
        }else{
            return String.format(Locale.getDefault(),"%." + decimalNumbers.get(index) + "f", value)  + unitStrs.get(index);
        }

    }
}
