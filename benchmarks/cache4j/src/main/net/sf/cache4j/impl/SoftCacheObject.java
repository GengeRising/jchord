/* =========================================================================
 * File: $Id: SoftCacheObject.java,v$
 *
 * Copyright (c) 2006, Yuriy Stepovoy. All rights reserved.
 * email: stepovoy@gmail.com
 *
 * =========================================================================
 */

package net.sf.cache4j.impl;

import java.lang.ref.SoftReference;

/**
 * ����� SoftCacheObject ��� ������ :) �������� ��� ���������� ��������.
 * ��� �������� ������ �� ������ ������������ ����� <code>java.lang.ref.SoftReference</code>
 *
 * @version $Revision: 1.0 $ $Date:$
 * @author Yuriy Stepovoy. <a href="mailto:stepovoy@gmail.com">stepovoy@gmail.com</a>
 **/

public class SoftCacheObject extends CacheObject {
// ----------------------------------------------------------------------------- ���������
// ----------------------------------------------------------------------------- �������� ������
// ----------------------------------------------------------------------------- ����������� ����������
// ----------------------------------------------------------------------------- ������������

    /**
     * �����������
     * @param objId ������������� ����������� �������
     */
    SoftCacheObject(Object objId) {
        super(objId);
    }

// ----------------------------------------------------------------------------- Public ������

    /**
     * ���������� ���������� ������
     */
    Object getObject() {
        return _obj==null ? null : ((SoftReference)_obj).get();
    }

    /**
     * ������������� ���������� ������
     */
    void setObject(Object obj) {
        _obj = obj==null ? null : new SoftReference(obj);
    }

// ----------------------------------------------------------------------------- Package scope ������
// ----------------------------------------------------------------------------- Protected ������
// ----------------------------------------------------------------------------- Private ������
// ----------------------------------------------------------------------------- Inner ������
}

/*
$Log: SoftCacheObject.java,v $
*/
