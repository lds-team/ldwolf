package bingo.com.screen.modelscreen;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bingo.com.R;
import bingo.com.base.BaseFragment;
import bingo.com.dialog.AddRegexDialog;
import bingo.com.screen.modelscreen.adapter.ModelAdapter;
import bingo.com.utils.Utils;
import butterknife.Bind;
import butterknife.OnClick;

public class ModelFragment extends BaseFragment<ModelFragment> {

    public static final String TAG = "ModelFragment";

    @Bind(R.id.model_list)
    ExpandableListView list;

    @Bind(R.id.model_addregex)
    Button addRegex;

    private List<String> header;
    private HashMap<Integer, String> content;

    @Override
    public String initTag() {
        return TAG;
    }

    @Override
    public String screenNane() {
        return "Tin Mẫu";
    }

    @Override
    public View onCreateFragmentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return View.inflate(getActivity(), R.layout.fragment_model, null);
    }

    @Override
    public void onViewFragmentCreated(View view, Bundle savedInstanceState) {
        header = new ArrayList<>();
        content = new HashMap<>();

        ModelAdapter adapter = new ModelAdapter(getActivity());
        adapter.setData(header, content);
        list.setAdapter(adapter);

        makeData();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        header.clear();
        content.clear();
    }

    private void makeData() {
        header.add("Format tin nhắn");
        content.put(0, "Tin nhắn phải được bắt đầu bằng đề/lô/xiên/xiên quay. Nếu không phần mềm sẽ báo lỗi.\nBỏ phải viết là bỏ hoặc bor\nBộ phải viết là bộ hoặc boj\nPhần mềm có thể đọc được cả có dấu và không dấu");

        header.add("Đề đầu 123, đít 123, tổng 123, chạm 20, bộ 01,02,03 x100");
        content.put(1, "Phần mềm có thể đọc được nhiều dạng trong 1 tin nhắn và cộng tổng lại giá trị của các số đánh, không bỏ trùng.");

        header.add("Xiên 01,02,03 05,06 10,11,12,13 x100");
        content.put(2, "Phần mềm có thể đọc được nhiều loại xiên trong 1 cú pháp. Xử lý với các ký tự đặc biệt .,- nhưng không quá 2 ký tự trên 1 cú pháp.");

        header.add("Xiên quay 01,02,03 04,05,06 10,11,12,13 x100");
        content.put(3, "Phần mềm có thể đọc được nhiều loại xiên trong 1 cú pháp. Xử lý với các ký tự đặc biệt .,- nhưng không quá 2 ký tự trên 1 cú pháp.");

        header.add("Đề dàn bỏ tổng 1,2 đầu 2,3 đít 4,5 bộ 50,60,70 x100");
        content.put(4, "Tất cả các cú pháp sau phần bỏ và trước phần giá tiền(x100), phần mềm sẽ hiểu là bỏ. Phần mềm có thể xử lý nhiều cú pháp bỏ liên tục.");

        header.add("Đề dàn 19 trùng tổng chắn bỏ đầu lẻ x100");
        content.put(5, "Lây dàn 19 có tổng chẵn và bỏ đi đầu lẻ. Phần mềm có thể xử lý nhiều trùng trong 1 tin nhắn.\nVD: Đề dàn 19 trùng tổng chẵn trùng đầu 1,2 bỏ đít lẻ. \n(Không khuyến khích).");

        header.add("Đề 100 số bỏ tổng 123 x100");
        content.put(6, "Lấy 100 số bỏ đi tổng 123.");

        header.add("Đề bộ 01,02,03,121,232 x100");
        content.put(7, "Lấy bộ 01,02,03. Bộ viết là bộ hoặc boj.");

        header.add("Đề bộ 01,02,03 số 010,020 x100");
        content.put(8, "Lấy đề bộ 01,02,03 đề 01,10,02,20.");

        header.add("Đề kép bằng, kép lệch, sát kép, 121,232,77,88 x100");
        content.put(9, "Lấy đề kép bằng, kép lệch, sát kép, đề 12,21,23,32,77,88.");

        header.add("Xiên ghép 2 01,02,03,04 x100");
        content.put(10, "Ghép xiên 2 với tổ hợp các số 01,02,03,04.");
    }

    @Override
    public ModelFragment instance() {
        return this;
    }

    @Override
    public void update() {

    }

    @OnClick(R.id.model_addregex)
    public void addRegex() {
        Utils.showAlertTwoButton(context, null, "Bạn đang sửa vào phần core hệ thống.\nChúng tôi sẽ không chịu trách nhiệm nếu xảy ra lỗi.", "Cancel", "Force Open", null, new Runnable() {
            @Override
            public void run() {
                showAddRegex();
            }
        });
    }

    public void showAddRegex() {
        AddRegexDialog dialog = new AddRegexDialog(context, false);
        dialog.show();
    }
}
