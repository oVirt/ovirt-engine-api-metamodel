/*
 * Copyright oVirt Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.ovirt.api.metamodel.tool;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

/**
 * This class is a buffer intended to simplify generation of CSV files.
 */
public class CsvBuffer {
    // The output file:
    private File file;

    // The headers of the columns:
    private String[] headers;

    // The rows of the file:
    private List<String[]> rows = new ArrayList<>();

    /**
     * Sets the output file.
     */
    public void setFile(File newFile) {
        file = newFile;
    }

    /**
     * Sets the headers.
     */
    public void setHeaders(String... newHeaders) {
        headers = newHeaders;
    }

    /**
     * Adds a row.
     */
    public void addRow(String... newRow) {
        rows.add(newRow);
    }

    /**
     * Writes the {@code .csv} file.
     *
     * @throws IOException if something fails while creating or writing the file
     */
    public void write() throws IOException {
        // Create the and all its parent if needed:
        FileUtils.forceMkdir(file.getParentFile());

        // Write the file:
        System.out.println("Writing CSV file \"" + file.getAbsolutePath() + "\".");
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), Charset.forName("UTF-8")))) {
            writeRow(writer, headers);
            for (String[] row : rows) {
                writeRow(writer, row);
            }
        }
    }

    private void writeRow(PrintWriter writer, String[] row) {
        boolean first = true;
        for (String col : row) {
            if (!first) {
                writer.print(',');
            }
            writer.print('\"');
            for (char c : col.toCharArray()) {
                if (c == '\"') {
                    writer.print(c);
                }
                writer.print(c);
            }
            writer.print('\"');
            first = false;
        }
        writer.println();
    }
}
