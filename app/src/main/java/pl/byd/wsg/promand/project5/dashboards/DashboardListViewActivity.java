package pl.byd.wsg.promand.project5.dashboards;

import android.app.ActionBar;
import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupMenu;

import java.util.List;

import pl.byd.wsg.promand.project5.R;
import pl.byd.wsg.promand.project5.add.AddScreenActivity;
import pl.byd.wsg.promand.project5.categories.CategoriesActivity;
import pl.byd.wsg.promand.project5.database.DataSource;
import pl.byd.wsg.promand.project5.menus.MenuActivity;
import pl.byd.wsg.promand.project5.model.ExpenseEntry;
import pl.byd.wsg.promand.project5.projects.ProjectActivity;

/**
 * Created by Miguel on 14-03-2014.
 */
public class DashboardListViewActivity extends ListActivity {
    DataSource dataSource;
    List<ExpenseEntry> expenseEntryList;
    private static final int EXPENSE_ENTRY_DETAIL_ACTIVITY = 1001;

    //SET UP BUTTON
    static final String LIGHT_BLUE="#33B5E5";
    Button btnListView;
    Button btnGraphView;
    Button btnFilteredBy;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_listview);
        ActionBar actionBar = getActionBar();
        actionBar.setHomeButtonEnabled(true); //this required API level 14  MIGUEL

        //SET UP BUTTON
        btnListView=(Button) findViewById(R.id.buttonGoToListView);
        btnListView.setBackgroundColor(Color.parseColor(LIGHT_BLUE));
        btnListView.setTextColor(Color.WHITE);
        btnGraphView=(Button) findViewById(R.id.buttonGoToGraphView);
        btnGraphView.setBackgroundColor(Color.WHITE);
        btnGraphView.setTextColor(Color.parseColor(LIGHT_BLUE));

        //FILTER BY
        btnFilteredBy = (Button) findViewById(R.id.filteredByButton);
        btnFilteredBy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(DashboardListViewActivity.this, btnFilteredBy);
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        Log.i("MenuItemClick", "Item Id"+item.getItemId());
                        if(item.getTitle().toString().equals("Projects")){
                            filteredByProjects();
                        }else{
                            filteredByCategories();
                        }
                        return true;
                    }
                });
                popup.show();//showing popup menu
            }
        });//closing the setOnClickListener method

    //instantiate DataSource
        dataSource=new DataSource(this);
        dataSource.open();
        expenseEntryList=dataSource.findAll();

        if (expenseEntryList.size()==0){
            expenseEntryList=dataSource.findAll();  /// NECESSARY??
        }
        refreshDisplay();
        Intent returnIntent = new Intent();
        setResult(RESULT_OK,returnIntent);
    }

    public void refreshDisplay(){
        ListView dataList=(ListView)findViewById(android.R.id.list);
        ArrayAdapter<ExpenseEntry> adapter=new ArrayAdapter<ExpenseEntry>(this, android.R.layout.simple_list_item_1,expenseEntryList);
        dataList.setAdapter(adapter);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId())
        {
            case R.id.menu_add:
                Intent GoToAddScreenIntent = new Intent(this, AddScreenActivity.class);
                startActivity(GoToAddScreenIntent);
                break;
            case android.R.id.home:
                Intent intent = new Intent(this, MenuActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void GoToListView(View v){
    }
    public void GoToGraphView(View v){
        Intent intent = new Intent(this, DashboardGraphActivity.class);
        //this.finish();
        startActivity(intent);
    }
    public void filteredByCategories(){
        Intent intent = new Intent(this, CategoriesActivity.class);
        startActivityForResult(intent, 2);
    }

    public void filteredByProjects(){
        Intent intent = new Intent(this, ProjectActivity.class);
        startActivityForResult(intent, 1);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if(resultCode == RESULT_OK){
                String result=data.getStringExtra("result");
                expenseEntryList=dataSource.findFiltered("project = \""+result+"\"" ,"amount ASC");
                refreshDisplay();
            }
            if (resultCode == RESULT_CANCELED) {
                Log.d("MS", "It's in   resultCode == RESULT_CANCELED");
            }
        }
        if (requestCode ==2){
            if (resultCode==RESULT_OK){
                String result=data.getStringExtra("result");
                expenseEntryList=dataSource.findFiltered("category = \""+result+"\"" ,"amount ASC");
                refreshDisplay();
            }
            if (resultCode == RESULT_CANCELED){
                Log.d("MS", "It's in   resultCode == RESULT_CANCELED");
            }
        }
    }//onActivityResult

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        ExpenseEntry expenseEntry=expenseEntryList.get(position);
        Intent intent = new Intent(this, ExpenseEntryDetailActivity.class);
        intent.putExtra(".model.ExpenseEntry",expenseEntry);

        startActivityForResult(intent, EXPENSE_ENTRY_DETAIL_ACTIVITY);
    }




}