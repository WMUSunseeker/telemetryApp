/**
 * Sunseeker Telemetry
 *
 * @author Alec Carpenter <alecgunnar@gmail.com>
 * @date July 2, 2016
 */

package Panel.LiveData;

import Data.Type.DataTypeInterface;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;
import java.util.*;


public class LiveDataPanel extends AbstractLiveDataPanel {
    final public static int ROW_HEIGHT = 30;

    protected JTable table;
    protected DefaultTableModel model;

    public LiveDataPanel () {
        TitledBorder border = BorderFactory.createTitledBorder(" Live Data ");

        setBorder(border);

        /*
         * Need a layout for the tables to show up
         */
        setLayout(new BorderLayout());

        /*
         * Create the table to display the data
         */
        createTable();
    }

    public void refresh () {
        int row = model.getRowCount() - 1;

        for (; row >= 0; row--) {
            model.removeRow(row);
        }

        addRows();

        model.fireTableDataChanged();
    }

    protected void createTable () { // Called in constructor.
        /*
         * Remove any existing tables
         */
        removeAll();

        /*
         * Create the table will headers
         */
        model = new DefaultTableModel(new String[] {
            "Data", "Units", "Minimum", "Current", "Maximum", "Color"
        }, 0);

        table = new JTable(model);

        table.setRowHeight(ROW_HEIGHT);

        TableColumn colors  = table.getColumnModel().getColumn(5);

        colors.setCellRenderer(new DefaultTableCellRenderer(){
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column){
                Component c = super.getTableCellRendererComponent(table,value,isSelected,hasFocus,row,column);
                c.setBackground((Color)value);
                c.setForeground((Color)value);
                return c;
            }
        });

        // making sure color column isnt super big, need to set preferred width of each column though.
        colors.setPreferredWidth(10);
        table.getColumnModel().getColumn(0).setPreferredWidth(110);
        table.getColumnModel().getColumn(1).setPreferredWidth(110);
        table.getColumnModel().getColumn(2).setPreferredWidth(105);
        table.getColumnModel().getColumn(3).setPreferredWidth(110);
        table.getColumnModel().getColumn(4).setPreferredWidth(110);

        /*
         * Each available data type will have its own row
         */
        addRows();

        /*
         * Create the scroll pane without a border
         */
        JScrollPane scrollable = new JScrollPane(table);

        scrollable.setBorder(BorderFactory.createEmptyBorder());

        /*
         * Add the scroll pane with the table to the panel
         */
        add(scrollable, BorderLayout.CENTER);
    }

    protected void addRows () {
        if (types == null)
            return;

        // Sort all the data types alphabetically, first by getting the values as a Collection<DataTypeInterface>
        Collection<DataTypeInterface> vals = types.values();

        // Then we convert to a List, so we can use sort() method.
        List<DataTypeInterface> valsList = new ArrayList<DataTypeInterface>(vals);

        // We call sort, with our comparator using the values from getDisplayName() method.
        valsList.sort(Comparator.comparing(DataTypeInterface::getDisplayName));

        for (DataTypeInterface type : valsList) {
            if (type.isEnabled()) {
                model.addRow(new Object[] {
                    type.getDisplayName(),
                    type.getDisplayUnits(),
                    (float) type.getMinimumValue(),
                    (float) type.getCurrentValue(),
                    (float) type.getMaximumValue(),
                    type.getColor()
                });
            }
        }
    }
}
