/********************************************************* begin of preamble
**
** Copyright (C) 2003-2012 Software- und Organisations-Service GmbH. 
** All rights reserved.
**
** This file may be used under the terms of either the 
**
**   GNU General Public License version 2.0 (GPL)
**
**   as published by the Free Software Foundation
**   http://www.gnu.org/licenses/gpl-2.0.txt and appearing in the file
**   LICENSE.GPL included in the packaging of this file. 
**
** or the
**  
**   Agreement for Purchase and Licensing
**
**   as offered by Software- und Organisations-Service GmbH
**   in the respective terms of supply that ship with this file.
**
** THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
** IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
** THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
** PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS
** BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
** CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
** SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
** INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
** CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
** ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
** POSSIBILITY OF SUCH DAMAGE.
********************************************************** end of preamble*/
package com.sos.VirtualFileSystem.WebDAV;

import java.io.InputStream;

import com.sos.JSHelper.Exceptions.JobSchedulerException;
import com.sos.VirtualFileSystem.common.SOSVfsTransferFileBaseClass;
import com.sos.i18n.annotation.I18NResourceBundle;


/**
 * @author KB
 *
 */
@I18NResourceBundle(baseName = "SOSVirtualFileSystem", defaultLocale = "en")
public class SOSVfsWebDAVFile extends SOSVfsTransferFileBaseClass {
	private String strFileName = null;
	/**
	 *
	 * \brief SOSVfsWebDAVFile
	 *
	 * \details
	 *
	 * @param pstrFileName
	 */
	public SOSVfsWebDAVFile(final String pstrFileName) {
		super(pstrFileName);
		strFileName = pstrFileName;
	}

	/**
	 *
	 * \brief read
	 *
	 * \details
	 *
	 * \return
	 *
	 * @param bteBuffer
	 * @return
	 */
	@Override
	public int read(final byte[] bteBuffer) {
		try {
			InputStream is = this.getFileInputStream();

			if (is == null) {
				throw new JobSchedulerException(SOSVfs_E_177.get());
			}
			return is.read(bteBuffer);
		}
		catch (Exception e) {
			RaiseException(e, SOSVfs_E_173.params("read", fileName));
			return 0;
		}
	}

	/**
	 *
	 * \brief read
	 *
	 * \details
	 *
	 * \return
	 *
	 * @param bteBuffer
	 * @param intOffset
	 * @param intLength
	 * @return
	 */
	@Override
	public int read(final byte[] bteBuffer, final int intOffset, final int intLength) {
		try {
			InputStream is = this.getFileInputStream();
			if (is == null) {
				throw new Exception(SOSVfs_E_177.get());
			}
			return is.read(bteBuffer, intOffset, intLength);
		}
		catch (Exception e) {
			RaiseException(e, SOSVfs_E_173.params("read", fileName));
			return 0;
		}
	}

	/**
	 *
	 * \brief write
	 *
	 * \details
	 *
	 * \return
	 *
	 * @param bteBuffer
	 * @param intOffset
	 * @param intLength
	 */

//	private final  OutputStream os = null;

	@Override
	public void write(final byte[] bteBuffer, final int intOffset, final int intLength) {
		try {
			if (objOutputStream == null) {
				objOutputStream = objVFSHandler.getOutputStream(strFileName);
			}
			if (objOutputStream == null) {
				throw new Exception(SOSVfs_E_147.get());
			}
			objOutputStream.write(bteBuffer, intOffset, intLength);
			((SOSVfsWebDAVOutputStream) objOutputStream).put();
		}
		catch (Exception e) {
			RaiseException(e, SOSVfs_E_173.params("write", fileName));
		}
	}
	
	
	@Override
	public void write(final byte[] bteBuffer) {
		try {
			if (objOutputStream == null) {
				objOutputStream = objVFSHandler.getOutputStream(strFileName);
			}
			if (objOutputStream == null) {
				throw new Exception(SOSVfs_E_147.get());
			}
			objOutputStream.write(bteBuffer);
			((SOSVfsWebDAVOutputStream) objOutputStream).put();
		}
		catch (Exception e) {
			RaiseException(e, SOSVfs_E_173.params("write", fileName));
		}
	}
	

}