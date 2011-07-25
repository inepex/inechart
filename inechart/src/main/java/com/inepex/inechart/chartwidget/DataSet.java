package com.inepex.inechart.chartwidget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.inepex.inechart.chartwidget.misc.ColorSet;
import com.inepex.inechart.chartwidget.piechart.Slice;
import com.inepex.inechart.chartwidget.properties.Color;


/**
 * Collection of data points to be visualized.
 * Stores some additional information. 
 * 
 * @author Miklós Süveges / Inepex Ltd.
 *
 */
public class DataSet {
	private static ColorSet colorSet = new ColorSet();
	/**
	 * @return the colorSet
	 */
	public static ColorSet getColorSet() {
		return colorSet;
	}

	/**
	 * @param colorSet the colorSet to set
	 */
	public static void setColorSet(ColorSet colorSet) {
		DataSet.colorSet = colorSet;
	}
	private static int autoNameMaxIndex = 0;
	private ArrayList<Double> yValues;
	private ArrayList<Double> xValues;
	private String name;
	private String description;
	private Color color;
	private double xMax;
	private double yMax;
	private double yMin;
	private double xMin;
	private boolean isUpToDate = false;
	/**
	 * affects only two-dimensional dataset
	 */
	private boolean isSortable = false;
	/**
	 * affects only two-dimensional dataset
	 */
	private boolean allowDuplicateXes = true;
	
	/**
	 * Creates an empty dataset, with
	 * {@link #isSortable()} = false, and
	 * {@link #allowDuplicateXes} = true default options 
	 */
	public DataSet(){
		xValues = new ArrayList<Double>();
		yValues = new ArrayList<Double>();
	}
	
	/**
	 * Creates an empty dataset with the given options
	 * @param isSortable see {{@link #setSortable(boolean)}}
	 * @param allowDuplicateXes see {@link #setAllowDuplicateXes(boolean)}
	 */
	public DataSet(boolean isSortable, boolean allowDuplicateXes) {
		this();
		this.isSortable = isSortable;
		this.allowDuplicateXes = allowDuplicateXes;
	}

	/**
	 * Creates a one dimension dataset from the given values.
	 * Any x values can be added later on with {@link #setxValues(ArrayList)} or {@link #addX(double)} methods 
	 * @param values will be contained by {@link #yValues}
	 */
	public DataSet(List<Double> values){
		this();
		setValues(values);
	}
	
	/**
	 * Creates a two dimension dataset (like a standard mathematical function) from the given values,
	 * {@link #allowDuplicateXes} is trivially set false
	 * {@link #isSortable} can be set later on (default false)
	 * @param dataMap
	 */
	public DataSet(Map<Double, Double> dataMap){
		this();
		allowDuplicateXes = false;
		setDataMap(dataMap);
	}
		
	public DataSet(ArrayList<Double> yValues, ArrayList<Double> xValues,
			String name, String description, Color color) {
		this.yValues = yValues;
		this.xValues = xValues;
		this.name = name;
		this.description = description;
		this.color = color;
		updateExtremes();
	}
	public ArrayList<Double> getyValues() {
		return yValues;
	}
	public void setyValues(ArrayList<Double> yValues) {
		if(yValues != null){
			this.yValues = yValues;
			update();
		}
	}
	public ArrayList<Double> getxValues() {
		return xValues;
	}
	public void setxValues(ArrayList<Double> xValues) {
		if(xValues != null){
			this.xValues = xValues;
			update();
		}
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * The name will represent this dataset in legend by default
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * @return the color
	 */
	public Color getColor() {
		return color;
	}
	/**
	 * Sets the {@link Color}, which will be used in visualizations by default 
	 * @param color the color to set
	 */
	public void setColor(Color color) {
		this.color = color;
	}
	public void addDataPair(double x, double y){
		addX(x);
		addY(y);
	}
	public void addX(double x){
		xValues.add(x);
		isUpToDate = false;
	}
	public void addY(double y){
		yValues.add(y);
		isUpToDate = false;
	}
	public void addyValues(List<Double> yValues){
		for(double y : yValues){
			this.yValues.add(y);
		}
		update();
	}
	public void addxValues(List<Double> xValues){
		for(double x : xValues){
			this.xValues.add(x);
		}
		update();
	}
	public void addDataMap(Map<Double, Double> dataMap){
		for(double x : dataMap.keySet()){
			xValues.add(x);
			yValues.add(dataMap.get(x));
		}
		update();
	}
	public void setDataMap(Map<Double, Double> dataMap){
		yValues.clear();
		xValues.clear();
		addDataMap(dataMap);
	}
	public Map<Double,Double> getDataMap(){
		TreeMap<Double, Double> dataMap = new TreeMap<Double, Double>();
		double x;
		for(int i=0;i<yValues.size();i++){
			if(xValues.size() != yValues.size()){
				x = i;
			}
			else{
				x = xValues.get(i);
			}
			dataMap.put(x, yValues.get(i));
		}
		return dataMap;
	}
	public List<Double> getValues(){
		return yValues;
	}
	public void setValues(List<Double> values){
		if(values != null){
			if(values instanceof ArrayList<?>){
				yValues = (ArrayList<Double>) values;
			}
			else{
				yValues.clear();
				addyValues(values);
			}
		}
	}
	/**
	 * Updates the dataset:
	 *  - sorts it if {@link #isSortable} is true
	 *  - eliminates duplicate entries if {@link #allowDuplicateXes} is false
	 *  - sets X and Y extremes
	 *  - generates a name if not exists
	 *  - sets the color
	 */
	public void update(){
		if(isUpToDate)
			return;
		updateExtremes();
		eliminateDuplicateEntries();
		sortCollections();
		isUpToDate = true;
		if(name == null){
			name = "Data Set No.: " + ++autoNameMaxIndex;
		}
		if(color == null){
			color = colorSet.getNextColor();
		}
	}
	protected void updateExtremes(){
		updatexExtremes();
		updateyExtremes();
	}
	protected void updateyExtremes(){
		if(yValues.isEmpty())
			return;
		yMax = Collections.max(yValues);
		yMin = Collections.min(yValues);
	}
	protected void updatexExtremes(){
		if(xValues.isEmpty())
			return;
		xMax = Collections.max(xValues);
		xMin = Collections.min(xValues);		
	}
	protected void sortCollections(){
		if(!isSortable || xValues.isEmpty() || xValues.size() > yValues.size())
			return;
		ArrayList<double[]> valuePairs = new ArrayList<double[]>();
		for(int i=0;i<xValues.size();i++){
			valuePairs.add(new double[]{xValues.get(i), yValues.get(i)});
		}
		Collections.sort(valuePairs, new Comparator<double[]>() {

			@Override
			public int compare(double[] arg0, double[] arg1) {
				if(arg0[0]>arg1[0])
					return 1;
				else if(arg0[0]<arg1[0])
					return -1;
				return 0;
			}
		});
		xValues.clear();
		yValues.clear();
		for(int i=0; i<valuePairs.size();i++){
			xValues.add(valuePairs.get(i)[0]);
			yValues.add(valuePairs.get(i)[1]);
		}
	}
	protected void eliminateDuplicateEntries(){
		if(allowDuplicateXes || xValues.isEmpty() || xValues.size() > yValues.size())
			return;
		ArrayList<double[]> valuePairs = new ArrayList<double[]>();
		for(int i=0;i<xValues.size();i++){
			double x = xValues.get(i);
			//we added the same x previously
			if(xValues.indexOf(x) != i){
				//overwrite older entry
				valuePairs.set(xValues.indexOf(x), new double[]{x, yValues.get(i)});
			}
			else{
				valuePairs.add(new double[]{xValues.get(i), yValues.get(i)});
			}
		}
		
		xValues.clear();
		yValues.clear();
		for(int i=0; i<valuePairs.size();i++){
			xValues.add(valuePairs.get(i)[0]);
			yValues.add(valuePairs.get(i)[1]);
		}
	}
	/**
	 * @return the xMax
	 */
	public double getxMax() {
		return xMax;
	}

	/**
	 * @return the yMax
	 */
	public double getyMax() {
		return yMax;
	}

	/**
	 * @return the yMin
	 */
	public double getyMin() {
		return yMin;
	}

	/**
	 * @return the xMin
	 */
	public double getxMin() {
		return xMin;
	}
	
	public List<double[]> getDataPairs(){
		ArrayList<double[]> dataPairs = new ArrayList<double[]>();
		for(int i=0;i<xValues.size();i++){
			dataPairs.add(new double[]{xValues.get(i), yValues.get(i)});
		}
		return dataPairs;
	}

	/**
	 * Tells whether X values can be sorted
	 * @return the isSortable
	 */
	public boolean isSortable() {
		return isSortable;
	}

	/**
	 * If set true the x elements will be sorted,
	 * and applied immediately.
	 * @param isSortable the isSortable to set
	 */
	public void setSortable(boolean isSortable) {
		this.isSortable = isSortable;
		if(isSortable)
			sortCollections();
	}

	/**
	 * Tells if the x elements must be unique or must not
	 * @return the allowDuplicateXes
	 */
	public boolean isAllowDuplicateXes() {
		return allowDuplicateXes;
	}

	/**
	 * If set true any newly added x (and its y) will overwrite the previous entry,
	 * and the dataSet will apply this change immediately (eliminates duplicate entries).
	 * @param allowDuplicateXes
	 */
	public void setAllowDuplicateXes(boolean allowDuplicateXes) {
		this.allowDuplicateXes = allowDuplicateXes;
		if(!allowDuplicateXes)
			eliminateDuplicateEntries();
	}
}
