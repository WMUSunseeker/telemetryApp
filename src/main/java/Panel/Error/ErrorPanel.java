package Panel.Error;

import Data.Type.ErrorType;
import Data.Type.SingleError;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.*;
import java.awt.*;
import java.util.Collection;

public class ErrorPanel extends AbstractErrorPanel {
    final public static int ROW_HEIGHT = 30;

    protected JTable errTable;
    protected DefaultTableModel errModel;

    protected JTable limTable;
    protected DefaultTableModel limModel;

    protected JTabbedPane tabs;

    public ErrorPanel () {
        TitledBorder border = BorderFactory.createTitledBorder(" Error Data ");

        setBorder(border);

        /*
         * Need a layout for the tables to show up
         */
        setLayout(new BorderLayout());

        /*
         * Create the table to display the data
         */

        tabs = new JTabbedPane();
        add(tabs);

        createErrTable();
        createLimTable();
    }

    public void refresh () {
        int row = errModel.getRowCount() - 1;

        for (; row >= 0; row--) {
            errModel.removeRow(row);
        }


        row = limModel.getRowCount() -1;

        for(; row >= 0; row--){
            limModel.removeRow(row);
        }

        addErrorRows();
        addLimRows();

        errModel.fireTableDataChanged();
    }

    protected void createErrTable(){

        errModel = new DefaultTableModel(new String[] {
                "Error", "Value"
        }, 0);

        errTable = new JTable(errModel);

        errTable.setRowHeight(ROW_HEIGHT);
        TableColumn errors  = errTable.getColumnModel().getColumn(1);

        errors.setCellRenderer(new DefaultTableCellRenderer(){
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column){
                Component c = super.getTableCellRendererComponent(table,value,isSelected,hasFocus,row,column);
                c.setBackground(value.equals(0.0) ? Color.RED : Color.GREEN);
                return c;
            }
        });

        addErrorRows();

        /*
         * Create the scroll pane without a border
         */
        JScrollPane scrollable = new JScrollPane(errTable);

        scrollable.setBorder(BorderFactory.createEmptyBorder());

        /*
         * Add the scroll pane with the table to the panel
         */
        JPanel panel = new JPanel();
        panel.add(scrollable);
        tabs.addTab("Errors", panel);
    }

    protected void createLimTable(){
        limModel = new DefaultTableModel(new String[] {
                "Limit", "Value"
        }, 0);

        limTable = new JTable(limModel);

        limTable.setRowHeight(ROW_HEIGHT);
        TableColumn limits  = limTable.getColumnModel().getColumn(1);

        limits.setCellRenderer(new DefaultTableCellRenderer(){
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column){
                Component c = super.getTableCellRendererComponent(table,value,isSelected,hasFocus,row,column);
                c.setBackground(value.equals(0.0) ? Color.RED : Color.GREEN);
                return c;
            }
        });

        addLimRows();

        /*
         * Create the scroll pane without a border
         */
        JScrollPane scrollable = new JScrollPane(limTable);

        scrollable.setBorder(BorderFactory.createEmptyBorder());

        /*
         * Add the scroll pane with the table to the panel
         */
        JPanel panel = new JPanel();
        panel.add(scrollable);
        tabs.addTab("Limits", panel);
    }

    protected void addErrorRows () {
        if (types == null)
            return;

        for (ErrorType type : types.values()) {
            if (type.isEnabled()) {
                Collection<SingleError> errs = type.getErrors().values();
                for (SingleError err : errs) {
                    errModel.addRow(new Object[] {
                            err.getName(),
                            err.getValue()
                    });
                }
            }
        }
    }

    protected void addLimRows(){
        if (types == null)
            return;

        for (ErrorType type : types.values()) {
            if (type.isEnabled()) {
                Collection<SingleError> lims = type.getLimits().values();
                for (SingleError lim : lims) {
                    System.out.println(lim);
                    limModel.addRow(new Object[] {
                            lim.getName(),
                            lim.getValue()
                    });
                }
            }
        }
    }
}
