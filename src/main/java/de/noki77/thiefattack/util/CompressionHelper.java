/*
 * This file is part of the project ThiefAttack, licensed under the
 * Creative Commons Attribution-NoDerivatives 4.0 International license.
 *
 * Copyright (c) 2016 Noki77 <dernoki77@gmail.com>
 * Copyright (c) contributors
 *
 * You should have received a copy of the license along with this
 * work. If not, see <http://creativecommons.org/licenses/by-nd/4.0/>.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THE TERMS
 * OF THIS CREATIVE COMMONS PUBLIC LICENSE ("CCPL" OR "LICENSE").
 * THE SOFTWARE IS PROTECTED BY COPYRIGHT AND/OR OTHER APPLICABLE LAW.
 * ANY USE OF THE WORK OTHER THAN AS AUTHORIZED UNDER THIS LICENSE
 * OR COPYRIGHT LAW IS PROHIBITED.
 *
 * BY EXERCISING ANY RIGHTS TO THE SOFTWARE PROVIDED HERE,
 * YOU ACCEPT AND AGREE TO BE BOUND BY THE TERMS OF THIS LICENSE.
 * TO THE EXTENT THIS LICENSE MAY BE CONSIDERED TO BE A CONTRACT,
 * THE LICENSOR GRANTS YOU THE RIGHTS CONTAINED HERE IN CONSIDERATION
 * OF YOUR ACCEPTANCE OF SUCH TERMS AND CONDITIONS.
 */

package de.noki77.thiefattack.util;

import core.org.apache.commons.io.IOUtils;
import core.org.apache.commons.io.output.ByteArrayOutputStream;
import core.org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class CompressionHelper {
    public static byte[] compress(String toCompress) throws IOException {
        try {
            if (StringUtils.isEmpty(toCompress)) {
                return new byte[]{};
            }
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            GZIPOutputStream compressionStream = new GZIPOutputStream(byteStream);
            compressionStream.write(toCompress.getBytes("UTF-8"));
            compressionStream.close();
            return byteStream.toByteArray();
        } catch (UnsupportedEncodingException e) {
            return new byte[]{};
        }
    }

    public static String decompress(byte[] toDecompress) throws IOException {
        try {
            if (toDecompress == null || toDecompress.length == 0) {
                return null;
            }

            GZIPInputStream stream = new GZIPInputStream(new ByteArrayInputStream(toDecompress));
            return IOUtils.toString(stream);
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }
}
