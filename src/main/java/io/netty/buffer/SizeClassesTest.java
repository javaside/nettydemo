package io.netty.buffer;

public class SizeClassesTest {
    public static void main(String[] args) {
        int pageSize = 8192;
        int maxOrder = 9;
        int pageShifts = Integer.SIZE - 1 - Integer.numberOfLeadingZeros(pageSize);
        int chunkSize = pageSize << maxOrder;

        System.out.println("pageSize: "+pageSize);
        System.out.println("maxOrder: "+maxOrder);
        System.out.println("pageShifts: "+pageShifts);
        System.out.println("chunkSize: "+chunkSize);

        System.out.println("");

        SizeClasses sizeClasses = new SizeClasses(pageSize,pageShifts,chunkSize,0);

        System.out.println("smallMaxSizeIdx: "+sizeClasses.smallMaxSizeIdx + ", smallMaxSize: " + sizeClasses.sizeIdx2size(sizeClasses.smallMaxSizeIdx));

        int psize = 0;
        int pdif = 0;
        for (int i = 0; i < sizeClasses.nSizes; i++) {
            int size = sizeClasses.sizeIdx2size(i);
            if(pdif != (size-psize)){
                System.out.println("\n======================================\n");
            }
            System.out.println("idx: " + i + " size: " + size + ", dif: " + (size-psize));
            pdif  = size-psize;
            psize = size;
        }

        System.out.println("\n========\n");

        for (int i = 0; i < sizeClasses.nPSizes; i++) {
            long pgSize = sizeClasses.pageIdx2size(i);
            long pages = (pgSize/pageSize);

            if(i>0){
                System.out.println(" end pages: " + (pages-1));
            }

            System.out.print("idx: " + i + " pgSize: " + pgSize + "," + "pages: " + pages);
        }

        System.out.println("\n=====\n");
        System.out.println("nSizes: " + sizeClasses.nSizes);
        System.out.println("nPSizes: " + sizeClasses.nPSizes);
        System.out.println("nSubpages: " + sizeClasses.nSubpages);


    }
}
