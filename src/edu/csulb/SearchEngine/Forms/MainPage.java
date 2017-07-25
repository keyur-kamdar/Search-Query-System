package edu.csulb.SearchEngine.Forms;

import java.awt.Component;
import java.awt.Container;

import java.util.ArrayList;

import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.InputMap;

import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.KeyStroke;
import javax.swing.UIManager;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

import org.eclipse.wb.swt.SWTResourceManager;

import csulb.edu.SearchEngine.Classes.DiskPositionalIndex;
import csulb.edu.SearchEngine.Classes.KGramIndex;
import csulb.edu.SearchEngine.Classes.PositionalInvertedIndex;
import csulb.edu.SearchEngine.Classes.Properties;
import csulb.edu.SearchEngine.Classes.RankedRetrieval;
import csulb.edu.SearchEngine.Classes.SearchWord;
import csulb.edu.SearchEngine.Classes.SimpleEngine;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;

import org.eclipse.swt.widgets.Control;



public class MainPage {

	//Property Class Object
	
	
	
	protected Shell shell;
	private Text txtLibraryPath;
	private Button btnGenerateLibrary;
	private Text txtSearch;
	private Button btnSearch;
	private Button btnBrowse;
	private ListViewer listViewer;
	private Label lblNoResultFound;
	private Label lblCount;
	private Button rbWildCard;
	private Button rbNearBy;
	
	int[] lstResultDocuments=new int[0];
	ArrayContentProvider objContentProvider=new ArrayContentProvider();
	private Button rbBooleanSearch;
	private Button btnVariableByteEncodeLib;
	private Button chkVariableByte;
	private Button rbRankRetrieval;
	public SimpleEngine objSimpleEngine;
	private DiskPositionalIndex objDiskPositionalIndex;
	private Button btnImpactOrdering;
	private Button chkImpactOrder;
	Group group2;
	
	/**
	 * @wbp.nonvisual location=544,179
	 */
	

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public MainPage()
	{
		objSimpleEngine=new SimpleEngine();
		objDiskPositionalIndex=new DiskPositionalIndex();
	}
	
	public static void main(String[] args) {
		
		
		try {
			MainPage window = new MainPage();
			window.open();
			
			
		
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}

	
	
	
	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		
		createContents(display);
		chkVariableByte = new Button(shell, SWT.CHECK);
		chkVariableByte.setBounds(361, 218, 93, 16);
		chkVariableByte.setText("Variable Byte");
		
		chkImpactOrder = new Button(shell, SWT.CHECK);
		chkImpactOrder.setText("Impact Order");
		chkImpactOrder.setBounds(476, 135, 93, 16);
		
		
	    
		
		
		shell.open();
		shell.layout();
		txtLibraryPath.setText(Properties.library_Directory_Path.toString());
		
		
		group2= new Group(shell, SWT.SHADOW_IN);
		
		btnGenerateLibrary = new Button(shell, SWT.NONE);
		btnGenerateLibrary.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SimpleEngine objSimpleEngine=new SimpleEngine();
				try
				{
					objSimpleEngine.GenerateLibrary();
					Properties.Lib=false;
					objDiskPositionalIndex=new DiskPositionalIndex();
					
					
				}
				catch (Exception ex)
				{
					System.out.println(ex);
				}
				
				
				
			}
		});
		btnGenerateLibrary.setBounds(10, 46, 98, 25);
		btnGenerateLibrary.setText("Generate Library");
		
		
		listViewer = new ListViewer(shell, SWT.BORDER | SWT.V_SCROLL);
		
		listViewer.addSelectionChangedListener(new  ISelectionChangedListener() {
			
			public void selectionChanged(SelectionChangedEvent event) {
			

				try {
					String path=event.getSelection().toString().replace("[", "");
					path=path.replace("]", "");
					OpenFile(Properties.library_Directory_Path+"\\"+path);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				// TODO Auto-generated method stub
				
			}
		});
		
		

		
		
		txtSearch = new Text(shell, SWT.BORDER);
		txtSearch.setBounds(9, 88, 533, 21);
		txtSearch.addKeyListener(new KeyListener() {
			
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub
				
				//SearchClick();
			}
		});
		
		
		Label lblFiles = new Label(shell, SWT.NONE);
		lblFiles.setBounds(10, 136, 55, 15);
		lblFiles.setText("FILES :");
		
				
		org.eclipse.swt.widgets.List list = listViewer.getList();
		list.setBounds(71, 132, 191, 349);
		
		Button btnClear = new Button(shell, SWT.NONE);
		btnClear.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				clearList();
			}
		});
		btnClear.setBounds(183, 492, 74, 25);
		btnClear.setText("CLEAR");
		
		lblNoResultFound = new Label(shell, SWT.NONE);
		lblNoResultFound.setBounds(71, 115, 119, 15);
		lblNoResultFound.setText("No Result Found.");
		
		Label lblLabel = new Label(shell, SWT.NONE);
		lblLabel.setBounds(71, 497, 45, 15);
		lblLabel.setText("Count :");
		
		lblCount = new Label(shell, SWT.NONE);
		lblCount.setBounds(122, 497, 55, 15);
		lblCount.setText("0");
		
		rbWildCard = new Button(shell, SWT.RADIO);
		rbWildCard.setBounds(361, 174, 75, 16);
		rbWildCard.setText("Wild Card");
		
		rbNearBy = new Button(shell, SWT.RADIO);
		rbNearBy.setBounds(361, 196, 119, 16);
		rbNearBy.setText("Near By Operator");
		
		rbBooleanSearch = new Button(shell, SWT.RADIO);
		rbBooleanSearch.setBounds(361, 152, 98, 16);
		rbBooleanSearch.setText("Boolean Search");
		
		btnVariableByteEncodeLib = new Button(shell, SWT.NONE);
		btnVariableByteEncodeLib.setEnabled(false);
		btnVariableByteEncodeLib.setText("Generate Variable Byte Library");
		btnVariableByteEncodeLib.setBounds(122, 46, 177, 25);
		btnVariableByteEncodeLib.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SimpleEngine objSimpleEngine=new SimpleEngine();
				try
				{
					objSimpleEngine.GenerateLibrary();
					Properties.VariableLib=false;
					objDiskPositionalIndex=new DiskPositionalIndex();
					
					
				}
				catch (Exception ex)
				{
					System.out.println(ex);
				}
				
				
				
			}
		});
		
		
		
		
		
		
		rbRankRetrieval = new Button(shell, SWT.RADIO);
		rbRankRetrieval.setSelection(true);
		rbRankRetrieval.setText("Rank Retrieval");
		rbRankRetrieval.setBounds(361, 132, 98, 16);
		
		btnImpactOrdering = new Button(shell, SWT.NONE);
		btnImpactOrdering.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SimpleEngine objSimpleEngine=new SimpleEngine();
				try
				{
					objSimpleEngine.GenerateLibrary();
					Properties.ImpactOrderLib=false;
					objDiskPositionalIndex=new DiskPositionalIndex();
					
					
				}
				catch (Exception ex)
				{
					System.out.println(ex);
				}
				
			}
		});
		btnImpactOrdering.setEnabled(false);
		btnImpactOrdering.setText("Generate Impact Ordered Libbrary");
		btnImpactOrdering.setBounds(315, 46, 208, 25);
		
		
		
		chkVariableByte.addSelectionListener(new SelectionListener() {
			
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				if(chkVariableByte.getSelection())
				{
					btnVariableByteEncodeLib.setEnabled(true);
					btnGenerateLibrary.setEnabled(false);
					Properties.VariableByteEncode=true;
					Properties.NoEncode=false;
					Properties.Impactorder=false;
					
					
				}
				else
				{
					btnVariableByteEncodeLib.setEnabled(false);
					btnGenerateLibrary.setEnabled(true);
					Properties.VariableByteEncode=false;
					Properties.NoEncode=true;
					Properties.Impactorder=false;
					
				}
				objDiskPositionalIndex=new DiskPositionalIndex();
				
			}
			
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		chkImpactOrder.addSelectionListener(new SelectionListener() {
			
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
				if(chkImpactOrder.getSelection())
				{
					btnImpactOrdering.setEnabled(true);
					btnVariableByteEncodeLib.setEnabled(false);
					btnGenerateLibrary.setEnabled(false);
					Properties.VariableByteEncode=false;
					Properties.NoEncode=false;
					Properties.Impactorder=true;
					chkVariableByte.setEnabled(false);
				}
				else
				{
					btnImpactOrdering.setEnabled(false);
					if(chkVariableByte.getSelection())
					{
						btnVariableByteEncodeLib.setEnabled(true);
						btnGenerateLibrary.setEnabled(false);
						Properties.VariableByteEncode=true;
						Properties.NoEncode=false;
						Properties.Impactorder=false;	
					}
					else
					{
						btnVariableByteEncodeLib.setEnabled(false);
						btnGenerateLibrary.setEnabled(true);
						Properties.VariableByteEncode=false;
						Properties.NoEncode=true;
						Properties.Impactorder=false;
					}
					chkVariableByte.setEnabled(true);
				}
				objDiskPositionalIndex=new DiskPositionalIndex();
				
			}
			
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		
		shell.setTabList(new Control[]{btnBrowse, txtLibraryPath, btnGenerateLibrary, txtSearch, btnSearch});
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
	
	void clearList()
	{
		if(listViewer.getInput()!=null)
		{
			listViewer.refresh();
			listViewer.setInput(null);	
			
		}
		
		
	}
	
	
	public void OpenFile (String file) throws Exception
	{
		Runtime r1=Runtime.getRuntime();
		
		r1.exec("notepad "+ file);
		
	}

	/**
	 * Create contents of the window.
	 *
	 *
	 */
	
	
	protected void createContents(Display d) {
		
		shell = new Shell(d);
		shell.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		
		
		shell.setSize(683, 565);
		shell.setText("SWT Application");
		
		
		
		
		
		btnBrowse = new Button(shell, SWT.NONE);
		btnBrowse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				
				
			SelectLibraryDirectory();
			Properties.Lib=true;
			Properties.VariableLib=true;
			Properties.ImpactOrderLib=true;
			
			SimpleEngine objSimpleEngine=new SimpleEngine();
			objDiskPositionalIndex=new DiskPositionalIndex();
		
				
				
			}
		});
		btnBrowse.setBounds(10, 5, 75, 25);
		btnBrowse.setText("Browse");
		
		btnSearch = new Button(shell, SWT.NONE);
		btnSearch.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
		
				if((Properties.NoEncode && Properties.Lib) ||(Properties.VariableByteEncode && Properties.VariableLib) || (Properties.Impactorder && Properties.ImpactOrderLib))
				{
					MessageDialog.openError(shell, "Gererate Library", "No Library Generated!");
					return;
				}
				
				String str=txtSearch.getText();
				
				if(!Query_Syntax_Check(str))
				{
					MessageDialog.openError(shell, "Syntex Error", "Enter Correct String Format!");
					return;
				}
				//Normal Way
				double startTime = new Date().getTime();
				
				if(rbWildCard.getSelection())
				{
					if(PositionalInvertedIndex.mIndex.size()==0)
					{
						MessageDialog.openError(shell, "Need a HashMap", "You have to have In-memory index to search a Wild-Card query!\nGenerate a Library.");
						return;
					}
					searchKgram();
					
				}
				else if(rbBooleanSearch.getSelection())
				{
					SearchClick();	
				}
				else if(rbNearBy.getSelection())
				{
					GetNear();
				}
				else if(rbRankRetrieval.getSelection())
				{
					rankedRetrieval();
				}
				double endTime   = new Date().getTime();
				double tt = endTime - startTime;
				
				tt=tt/1000;
				System.out.println(tt);
				
					
				
				
				
				
			}
		});
		btnSearch.setBounds(559, 86, 75, 25);
		btnSearch.setText("SEARCH");
		
		
		txtLibraryPath = new Text(shell, SWT.BORDER);
		txtLibraryPath.setEditable(false);
		txtLibraryPath.setBounds(105, 9, 521, 21);
		shell.setDefaultButton(btnSearch);
		InputMap im = (InputMap)UIManager.get("Button.focusInputMap");
		im.put( KeyStroke.getKeyStroke( "ENTER" ), "pressed" );
		im.put( KeyStroke.getKeyStroke( "released ENTER" ), "released" );
		
		

	}

	protected void rankedRetrieval() {
		clearList();
		lstResultDocuments=new int[0];
		lblNoResultFound.setVisible(false);
		
		String query = txtSearch.getText();
		List<Integer> lst=new ArrayList<Integer>();
		RankedRetrieval rankedRetrieval =  new RankedRetrieval(objDiskPositionalIndex);
		if(Properties.Impactorder)
		{
			lst=rankedRetrieval.ProcessRankRetrieval_ImpactOrder(query);
		}
		else
		{
			lst=rankedRetrieval.ProcessRankRetrieval(query);	
		}
		
		
		lstResultDocuments=new int[lst.size()];
		for(int i=0;i<lst.size();i++)
		{
			lstResultDocuments[i]=lst.get(i);
		}
		DisplayResult(lstResultDocuments);
	}




	void SelectLibraryDirectory()
	{
		
		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(Properties.library_Directory_Path.toFile());
		chooser.setDialogTitle("Select a Directory for Library");
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		
		removeFileTypeComponents(chooser);
		

		
		if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			
			Properties.library_Directory_Path=chooser.getSelectedFile().toPath();
			txtLibraryPath.setText(Properties.library_Directory_Path.toString());
		} 
		
		
	}
	
	void GetNear()
	{
		clearList();
		lstResultDocuments=new int[0];
		lblNoResultFound.setVisible(false);
		SearchWord objSearchWord=new SearchWord(objDiskPositionalIndex);
		
		lstResultDocuments=objSearchWord.GetNear(txtSearch.getText());
		DisplayResult(lstResultDocuments);
		
		
	}
	
	void searchKgram(){
		clearList();
		lstResultDocuments=new int[0];
		lblNoResultFound.setVisible(false);
		
		
		
		String str=txtSearch.getText();
		SearchWord objSearchWord;
		KGramIndex kGramIndex = new KGramIndex();
		ArrayList<String> wildCards = new ArrayList<String>();
		//List<String> lstFileName=new ArrayList<String>();
		
		wildCards = kGramIndex.GenKGramIndex(str);
		List<Integer> lst = kGramIndex.searchForQuery(wildCards);
		lstResultDocuments=new int[lst.size()];
		for(int i=0;i<lst.size();i++)
		{
			lstResultDocuments[i]=lst.get(i);
		}
		
		DisplayResult(lstResultDocuments);
	}
	
	boolean Query_Syntax_Check(String token)
	{		
		if(rbBooleanSearch.getSelection())
		{
			Matcher Error_In_Plus=Pattern.compile("[\\(](.)*[\\+](.)*[\\)]").matcher(token);
			if(Error_In_Plus.matches())
			{
				return false;
			}
			Pattern SysError1 = Pattern.compile("[\\(].+?[^\\)]");
			Pattern SysError2=Pattern.compile("[^\\(].+?[\\)]");
			Pattern SysError3 = Pattern.compile("[\"].+?[^\"]");
			Pattern SysError4 = Pattern.compile("[^\"].+?[\"]");
			if (token.contains("+")) 
			{
				String[] lst = token.split("[+]");
				for (int i = 0; i < lst.length; i++) 
				{
					if (SysError1.matcher(lst[i].trim()).matches() || SysError2.matcher(lst[i].trim()).matches() 
					|| SysError3.matcher(lst[i].trim()).matches() || SysError4.matcher(lst[i].trim()).matches()) 
					{
						return false;
					}	
				}
			}
			else 
			{
				Matcher m = Pattern.compile("(\"[^\\(\\)]+?\"|\\([^\"]+?\\))\\s*").matcher(token);
				List<String> lst=new ArrayList<String>();
				while (m.find())
				{
					token=token.replace(m.group(), "");
				    lst.add(m.group().toLowerCase()); 
				}
				if(token.contains("(")|| token.contains(")")|| token.contains("\""))
				{
					return false;
				}
			}
		}
		else if(rbNearBy.getSelection())
		{
			Matcher Check_Near=Pattern.compile(".*?(near/)(\\d+).*").matcher(token);
			
			if(!Check_Near.matches())
			{
				return false;
			}
			
		}
		return true;
	}
	
	void SearchClick()
	{
		
		clearList();
		lstResultDocuments=new int[0];
		lblNoResultFound.setVisible(false);
		
		
		String str=txtSearch.getText();
		SearchWord objSearchWord;
		
		objSearchWord=new SearchWord(objDiskPositionalIndex);
		
		
		lstResultDocuments=objSearchWord.SearchResult(str);
		
		DisplayResult(lstResultDocuments);
		
		
		
		
		
	}
	
	void DisplayResult(int[] lstResultDocuments)
	{
		List<String> lstFileName=new ArrayList<String>();
		for(int i=0;i<lstResultDocuments.length;i++)
		{
			lstFileName.add(SimpleEngine.listOfFiles[lstResultDocuments[i]].getName());
			
		}
		listViewer.setContentProvider(new ArrayContentProvider());
		listViewer.setInput(lstFileName);
		if(lstResultDocuments.length<=0)
		{
			lblNoResultFound.setVisible(true);
			
			
		}
		lblCount.setText(String.valueOf(lstFileName.size()));
	}
	
	void removeFileTypeComponents(Container con) 
	{
		Component[] components = con.getComponents();
		for (Component component : components) 
		{
			if (component instanceof JComboBox) 
			{
				Object sel = ((JComboBox) component).getSelectedItem();
				if (sel.toString().contains("AcceptAllFileFilter")) 
				{
					component.setVisible(false);

				}
			}
			if (component instanceof JLabel) 
			{
				String text = ((JLabel) component).getText();
				if (text.equals("Files of Type:")) 
				{
					component.setVisible(false);

				}
			}
			if (component instanceof Container) 
			{
				removeFileTypeComponents((Container) component);
			}
		}
	}
}
