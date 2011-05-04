package org.apache.stanbol.entityhub.indexing;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.stanbol.entityhub.indexing.core.Indexer;
import org.apache.stanbol.entityhub.indexing.core.IndexerFactory;

/**
 * Command Line Utility for indexing. If not other specified the configuration
 * is expected under {workingdir}/indexing.
 * @author Rupert Westenthaler
 *
 */
public class Main {
    private static final Options options;
    static {
        options = new Options();
        options.addOption("h", "help", false, "display this help and exit");
        options.addOption("c","chunksize",true, 
            String.format("the number of documents stored to the Yard in one chunk (default: %s)",
                Indexer.DEFAULT_CHUNK_SIZE));
    }
    /**
     * @param args
     * @throws ParseException 
     */
    public static void main(String[] args) throws ParseException {
        CommandLineParser parser = new PosixParser();
        CommandLine line = parser.parse(options, args);
        args = line.getArgs();
        if(line.hasOption('h') || args.length <= 0){
            printHelp();
            System.exit(0);
        }
        Indexer indexer;
        IndexerFactory factory = IndexerFactory.getInstance();
        String path = null;
        if(args.length > 1){
            path = args[1];
        }
        if("init".equalsIgnoreCase(args[0]) ||
                "index".equalsIgnoreCase(args[0])){
            if(path != null){
                indexer = factory.create(path);
            } else {
                indexer = factory.create();
            }
            if(line.hasOption('c')){
                int cunckSize = Integer.parseInt(line.getOptionValue('c'));
                indexer.setChunkSize(cunckSize);
            }
            if("index".equalsIgnoreCase(args[0])){
                indexer.index();
            }
        } else {
            System.err.println("Unknown command "+args[0]+" (supported: init,index)\n\n");
            printHelp();
        }
        System.exit(0);
    }
    /**
     * 
     */
    private static void printHelp() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(
            "java -Xmx{size} -jar org.apache.stanbol.indexing.core-*" +
            "-jar-with-dependencies.jar [options] init|index [configDir]",
            "Indexing Commandline Utility: \n"+
            "  size:  Heap requirements depend on the dataset and the configuration.\n"+
            "         1024m should be a reasonable default.\n" +
            "  init:  Initialise the configuration with the defaults \n" +
            "  index: Needed to start the indexing process\n" +
            "  configDir: the path to the configuration directory (default:" +
            " user.dir)",
            options,
            null);
    }

}
