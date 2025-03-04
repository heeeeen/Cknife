package com.ms509.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JTextField;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;

import com.ms509.ui.panel.FileManagerPanel;
import com.ms509.util.Common;
import com.ms509.util.Safe;

/*
 * 通过重写getColumnClass方法，在单元格直接设置图标
 * @author Chora
 */
public class RightTableModel extends AbstractTableModel {
	private boolean isEdit = false;
	private FileManagerPanel filemanagerpanel;

	public FileManagerPanel getFilemanagerpanel() {
		return filemanagerpanel;
	}

	public void setFilemanagerpanel(FileManagerPanel filemanagerpanel) {
		this.filemanagerpanel = filemanagerpanel;
	}

	public boolean isEdit() {
		return isEdit;
	}

	public void setEdit(boolean isEdit) {
		this.isEdit = isEdit;
	}

	private Vector<String> title;
	private Vector<Vector> datas;

	public RightTableModel(String[] filedicts) {

		// TODO Auto-generated constructor stub
		datas = new Vector<Vector>();
		title = new Vector<String>();
		title.add("是否");
		title.add("文件");
		title.add("时间");
		title.add("大小");
		title.add("属性");
		for (String tmp : filedicts) {
			String[] s = tmp.split("\t");
			String name = s[0];
			Vector data = new Vector();
				if (!name.equals("./") && !name.equals("../")) {
					if (name.charAt(s[0].length() - 1) == '/') {
						// data.add("isdict");
						data.add(new ImageIcon(getClass().getResource(
								"/com/ms509/images/folder.png")));
						data.add(name.substring(0, name.length() - 1));
						data.add(s[1]);
						data.add(s[2]);
						data.add(s[3]);
					} else {
						// data.add("isfile");
						data.add(new ImageIcon(getClass().getResource(
								"/com/ms509/images/file.png")));
						data.add(name);
						data.add(s[1]);
						data.add(s[2]);
						data.add(s[3]);
					}
					datas.add(data);
				}
		}

	}

	@Override
	public Class getColumnClass(int columnIndex) {
		// TODO Auto-generated method stub
		if (this.getRowCount() == 0) {
			return super.getColumnClass(columnIndex);
		} else {
			return getValueAt(0, columnIndex).getClass();
		}
	}

	@Override
	public String getColumnName(int column) {
		// TODO Auto-generated method stub
		return this.title.get(column);
	}

	@Override
	public int getColumnCount() {
		// TODO Auto-generated method stub
		return this.title.size();
	}

	@Override
	public int getRowCount() {
		// TODO Auto-generated method stub
		return datas.size();
	}

	@Override
	public Object getValueAt(int row, int column) {
		// TODO Auto-generated method stub
		return datas.get(row).get(column);
	}

	public void addRow(Vector<String> vector) {
		this.datas.add(vector);
		this.fireTableDataChanged();
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		// TODO Auto-generated method stub
		Vector data = new Vector();
		String oldname = "";
		String newname = "";
		String newfolder = "";
		String tmp = filemanagerpanel.getPath().getText();
		String path = Common.getAbsolutePath(tmp);
		for (int i = 0; i < this.getColumnCount(); i++) {
			if (i == 1) {
				oldname = this.datas.get(rowIndex).get(i).toString();
				newname = aValue.toString();
				newfolder = oldname;
				data.add(aValue);
			} else {
				data.add(this.datas.get(rowIndex).get(i));
			}
		}
		if (newfolder.equals("newFolder")) {
			if (aValue.equals(newfolder) || aValue.equals("")) {
				this.remove(this.getRowCount() - 1);
			} else {
				Vector exists = new Vector();
				for (Vector vec : this.datas) {
					exists.add(vec.get(1));
				}
				if (exists.contains(aValue)) {
					filemanagerpanel.getStatus().setText("目录已存在");
					this.remove(this.getRowCount() - 1);
				} else {
					String isnewdict = filemanagerpanel.getFm()
							.doAction("newdict",
									path + aValue.toString() + Safe.SYSTEMSP);
					if (isnewdict.equals("1")) {
						this.datas.set(rowIndex, data);
						filemanagerpanel.getStatus().setText("操作完成");
					} else {
						filemanagerpanel.getStatus().setText("操作失败");
					}
				}

			}
		} else {
			String isrename = filemanagerpanel.getFm().doAction("rename",
					path + oldname, path + newname);
			if (isrename.equals("1")) {
				this.datas.set(rowIndex, data);
				filemanagerpanel.getStatus().setText("操作完成");
			} else {
				filemanagerpanel.getStatus().setText("操作失败");
			}
		}

	}

	public void remove(int id) {
		this.datas.remove(id);
		this.fireTableDataChanged();
	}

	public void update(int id, Vector vector) {
		this.datas.set(id, vector);
		this.fireTableDataChanged();
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		// TODO Auto-generated method stub
		return this.isEdit;
	}
}
