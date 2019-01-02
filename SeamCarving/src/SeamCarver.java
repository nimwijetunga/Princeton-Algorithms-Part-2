import java.awt.Color;

import edu.princeton.cs.algs4.AcyclicSP;
import edu.princeton.cs.algs4.DijkstraSP;
import edu.princeton.cs.algs4.DirectedEdge;
import edu.princeton.cs.algs4.EdgeWeightedDigraph;
import edu.princeton.cs.algs4.Picture;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.Stopwatch;

public class SeamCarver {
	
   private Picture picture;
	
   public SeamCarver(Picture picture)  {
	   if(picture == null) {
		   throw new java.lang.IllegalArgumentException("Null Picture!");
	   }
	   this.picture = picture;
   }              // create a seam carver object based on the given picture
   
   public Picture picture() {
	   return this.picture;
   }                          // current picture
   
   public int width() {
	   return this.picture.width();
   }                           // width of current picture
   public int height()  {
	   return this.picture.height();
   }                         // height of current picture
   
   private double sqr(int x) {
	   return Math.pow(x, 2);
   }
   
   private double grad(Color one, Color two) {
	   return sqr(one.getRed() - two.getRed()) + sqr(one.getGreen() - two.getGreen()) + sqr(one.getBlue() - two.getBlue()); 
   }
   
   public double energy(int x, int y)   {
	   if(x < 0 || x >= this.width() || y < 0 || y >= this.height()) {
		   throw new java.lang.IllegalArgumentException("Invalid (x,y) pair!");
	   }
	   if(x == 0 || y == 0 || x == this.width() - 1 || y == this.height() - 1) {
		   return 1000;
	   }
	   double xGrad = grad(this.picture.get(x + 1, y), this.picture.get(x - 1, y));
	   double yGrad = grad(this.picture.get(x, y + 1), this.picture.get(x, y - 1));
	   return Math.sqrt(xGrad + yGrad);
   }            // energy of pixel at column x and row y
   
   private boolean inBounds(int x, int y) {
	   if(x < 0 || x >= this.width() || y < 0 || y >= this.height()) {
		   return false;
	   }
	   return true;
   }
   
   private int getIndex(int i, int j, int numCol) {
	   return (j * numCol + i) + 1;
   }
   
   private EdgeWeightedDigraph createGraphVert(Picture picture) {
	   int width = picture.width(), height = picture.height();
	   EdgeWeightedDigraph dg = new EdgeWeightedDigraph((width * height) + 2);
	   for(int i = 0 ; i < width; i++) {
		   dg.addEdge(new DirectedEdge(0,i+1,1000));
		   for(int j = 0; j < height; j++) {
			   int tail = getIndex(i,j,width);
			   if(inBounds(i, j + 1)) {
				   dg.addEdge(new DirectedEdge(tail, getIndex(i, j+1,width), energy(i,j+1)));
			   }
			   if(inBounds(i + 1, j + 1)) {
				   dg.addEdge(new DirectedEdge(tail, getIndex(i+1,j+1,width), energy(i+1,j+1)));
			   }
			   if(inBounds(i - 1, j + 1)) {
				   dg.addEdge(new DirectedEdge(tail, getIndex(i-1,j+1,width), energy(i-1,j+1)));
			   }
			   if(j == height - 1) {
				   dg.addEdge(new DirectedEdge(getIndex(i,j,width),(width*height) + 1,1));
			   }
		   }
	   }
	   return dg;
   }
   
   private EdgeWeightedDigraph createGraphHoriz(Picture picture) {
	   int width = picture.width(), height = picture.height();
	   EdgeWeightedDigraph dg = new EdgeWeightedDigraph((width * height)+2);
	   int count = 0;
	   int x = 1;
	   int y = width;
	   while(count < height) {
		   dg.addEdge(new DirectedEdge(0,x,1000));
		   dg.addEdge(new DirectedEdge(y,(width*height)+1,1));
		   y += width;
		   x += width;
		   count++;
	   }
	   
	   for(int i = 0 ; i < width; i++) {
		   for(int j = 0; j < height; j++) {
			   int tail = getIndex(i,j,width);
			   if(inBounds(i + 1, j)) {
				   dg.addEdge(new DirectedEdge(tail, getIndex(i+1, j,width), energy(i+1,j)));
			   }
			   if(inBounds(i + 1, j + 1)) {
				   dg.addEdge(new DirectedEdge(tail, getIndex(i+1,j+1,width), energy(i+1,j+1)));
			   }
			   if(inBounds(i + 1, j - 1)) {
				   dg.addEdge(new DirectedEdge(tail, getIndex(i+1,j-1,width), energy(i+1,j-1)));
			   }
		   }
	   }
	   return dg;
   }
   
   public int[] findHorizontalSeam() {
	   AcyclicSP sp;
	   EdgeWeightedDigraph dg = createGraphHoriz(this.picture);
	   int seam[] = new int[this.width()];
	   Iterable<DirectedEdge> edges = null;
	   sp = new AcyclicSP(dg,0);
	   edges = sp.pathTo((this.width() * this.height() + 1));

	   if(edges != null) {
		   int count = 0;
		   DirectedEdge eCur = null;
		   for(DirectedEdge e : edges) {
			   if(e.from() == 0 || e.to() == (this.width() * this.height() + 1))
				   continue;
			   eCur = e;
			   seam[count] = (e.from() - 1) / this.width();
			   count++;
		   }
		   if(eCur != null) {
			   seam[this.width() - 1] = (eCur.to() - 1) / this.width();
		   }
	   }
	   return seam;
   }             // sequence of indices for horizontal seam
   
   public int[] findVerticalSeam() {
	   AcyclicSP sp;
	   EdgeWeightedDigraph dg = createGraphVert(this.picture);
	   int seam[] = new int[this.height()];
	   Iterable<DirectedEdge> edges = null;
	   sp = new AcyclicSP(dg,0);
	   edges = sp.pathTo((this.width() * this.height() + 1));
	   
	   if(edges != null) {
		   int count = 0;
		   DirectedEdge eCur = null;
		   for(DirectedEdge e : edges) {
			   if(e.from() == 0 || e.to() == (this.width() * this.height() + 1))
				   continue;
			   eCur = e;
			   seam[count] = (e.from() - 1) % this.width();
			   count++;
		   }
		   if(eCur != null) {
			   seam[this.height() - 1] = (eCur.to() - 1) % this.width();
		   }
	   }
	   return seam;
   }                // sequence of indices for vertical seam
   
   public void removeHorizontalSeam(int[] seam) {
	   if(seam == null || seam.length != this.width() || this.height() <= 1) {
		   throw new java.lang.IllegalArgumentException("Invalid Seam Array!");
	   }
	   
	   for (int i = 1; i < this.width(); i++)
		      if (Math.abs(seam[i] - seam[i - 1]) > 1)
		        throw new IllegalArgumentException("absolute difference is not at most 1!");
	   
	   Picture newPic = new Picture(this.width(), this.height() - 1);
	   for(int i = 0; i < this.width(); i++) {
		   int k = 0;
		   for(int j = 0; j < this.height(); j++) {
			   if(seam[i] != j) {
				   newPic.setRGB(i, k++, this.picture.getRGB(i, j));
			   }
		   }
	   }
	   this.picture = newPic;
   }   // remove horizontal seam from current picture
   
   // remove vertical seam from current picture
   public void removeVerticalSeam(int[] seam) {
     if (seam == null) throw new IllegalArgumentException("Expected non-null seam");
     if (seam.length != height()) throw new IllegalArgumentException("Expected seam with length " + height());

     for (int i = 1; i < height(); i++)
       if (Math.abs(seam[i] - seam[i - 1]) > 1)
         throw new IllegalArgumentException("Expected adjacent elements of seam with have a absolute difference of at most 1");

     if (width() <= 1)
       throw new IllegalArgumentException("Cannot remove vertical seam on width <= 1");

     Picture np = new Picture(width() - 1, height());
     for (int j = 0; j < height(); j++) {
       for (int i = 0, k = 0; i < width(); i++) {
         if (i != seam[j]) {
           np.setRGB(k++, j, picture.getRGB(i, j));
         }
       }
     }
     this.picture = np;
   }   // remove vertical seam from current picture
   
   public static void main(String[] args) {
	   
//	   SeamCarver sc = new SeamCarver(new Picture("6x5.png"));
//	   int [] seam = sc.findHorizontalSeam();
//	   for(int i : seam) {
//		   System.out.println(i);
//	   }
//	   System.out.println(sc.createGraphVert(new Picture("6x5.png")));
//       if (args.length != 3) {
//           StdOut.println("Usage:\njava ResizeDemo [image filename] [num cols to remove] [num rows to remove]");
//           return;
//       }

       Picture inputImg = new Picture("HJocean.png");
       int removeColumns = Integer.parseInt("1");
       int removeRows = Integer.parseInt("3"); 

       StdOut.printf("image is %d columns by %d rows\n", inputImg.width(), inputImg.height());
       SeamCarver sc = new SeamCarver(inputImg);

       Stopwatch sw = new Stopwatch();

       for (int i = 0; i < removeRows; i++) {
           int[] horizontalSeam = sc.findHorizontalSeam();
           sc.removeHorizontalSeam(horizontalSeam);
       }

       for (int i = 0; i < removeColumns; i++) {
           int[] verticalSeam = sc.findVerticalSeam();
           sc.removeVerticalSeam(verticalSeam);
       }
       Picture outputImg = sc.picture();
       
       StdOut.printf("new image size is %d columns by %d rows\n", sc.width(), sc.height());

       StdOut.println("Resizing time: " + sw.elapsedTime() + " seconds.");
       inputImg.show();
       outputImg.show();
   }
}