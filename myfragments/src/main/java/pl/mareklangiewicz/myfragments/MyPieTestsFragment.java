package pl.mareklangiewicz.myfragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import pl.mareklangiewicz.myutils.MyMathUtils;
import pl.mareklangiewicz.myviews.MyPie;


public final class MyPieTestsFragment extends MyFragment implements View.OnClickListener {

    private MyPie pie1;
    private MyPie pie2;
    private MyPie pie3;
    private MyPie pie4;
    private MyPie mMyHeaderPie;

    public MyPieTestsFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.my_pie_tests_fragment, container, false);


        pie1 = (MyPie) root.findViewById(R.id.pie1);
        pie2 = (MyPie) root.findViewById(R.id.pie2);
        pie3 = (MyPie) root.findViewById(R.id.pie3);
        pie4 = (MyPie) root.findViewById(R.id.pie4);

        pie1.setOnClickListener(this);
        pie2.setOnClickListener(this);
        pie3.setOnClickListener(this);
        pie4.setOnClickListener(this);

        inflateHeader(R.layout.my_pie_tests_header);

        mMyHeaderPie = (MyPie) getHeader().findViewById(R.id.header_pie);

        return root;
    }


    @Override
    public void onClick(View v) {
        if( v instanceof MyPie) {
            MyPie pie = (MyPie) v;
            float to = MyMathUtils.getRandomFloat(pie.getFrom(), pie.getMaximum());
            pie.setTo(to);
            log.i("[SNACK]MyPie:to = %f", to);
        }
    }
}
