<xsl:stylesheet
	version="2.0"
	xmlns="http://www.w3.org/1999/xhtml"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:chord="http://chord.stanford.edu/">

<xsl:include href="misc.xsl"/>

<xsl:function name="chord:group_races">
	<xsl:param name="list"/>
	<xsl:param name="groupingAttrName"/>
	<xsl:param name="desc"/>
	<html>
	<head>
		<title><xsl:value-of select="$desc"/></title>
   		<link rel="stylesheet" href="style.css" type="text/css"/>
	</head>
	<body>
	<table class="summary">
	<colgroup>
		<col width="6%"/>
		<col width="15%"/>
		<col width="32%"/>
		<col width="15%"/>
		<col width="32%"/>
	</colgroup>
		<tr>
			<td colspan="5" class="head1"><xsl:value-of select="$desc"/></td>
		</tr>
		<tr>
			<td class="head2center" rowspan="2">Details</td>
			<td class="head2center" colspan="2">Trace 1</td>
			<td class="head2center" colspan="2">Trace 2</td>
		</tr>
		<tr>
			<td class="head2center">Thread</td>
			<td class="head2center">Memory Access</td>
			<td class="head2center">Thread</td>
			<td class="head2center">Memory Access</td>
		</tr>
	<xsl:for-each-group select="$list" group-by="@*[name() = $groupingAttrName]">
		<xsl:variable name="group_id" select="position()"/>
		<tr>
			<td class="head3" colspan="5"><xsl:value-of select="$group_id"/>. Dataraces on
				<xsl:apply-templates select="id(current-grouping-key())"/>
			</td>
		</tr>
		<xsl:for-each select="current-group()">
			<tr>
				<xsl:variable name="TCE1id" select="@TCE1id"/>
				<xsl:variable name="TCE2id" select="@TCE2id"/>
				<xsl:variable name="TCE1elem" select="id($TCE1id)"/>
				<xsl:variable name="TCE2elem" select="id($TCE2id)"/>
				<xsl:variable name="T1elem" select="id($TCE1elem/@Tid)"/>
				<xsl:variable name="C1elem" select="id($TCE1elem/@Cid)"/>
				<xsl:variable name="E1elem" select="id($TCE1elem/@Eid)"/>
				<xsl:variable name="T2elem" select="id($TCE2elem/@Tid)"/>
				<xsl:variable name="C2elem" select="id($TCE2elem/@Cid)"/>
				<xsl:variable name="E2elem" select="id($TCE2elem/@Eid)"/>
				<td><a href="race_{$TCE1id}_{$TCE2id}.html"><xsl:value-of select="$group_id"/>.<xsl:value-of select="position()"/></a></td>
                <td><xsl:apply-templates select="$T1elem"/></td>
				<td><xsl:apply-templates select="$E1elem"/> <br/>
                    Context: <xsl:apply-templates select="$C1elem"/> <br/>
				</td>
                <td><xsl:apply-templates select="$T2elem"/></td>
				<td><xsl:apply-templates select="$E2elem"/> <br/>
                	Context: <xsl:apply-templates select="$C2elem"/> <br/>
				</td>
			</tr>
		</xsl:for-each>
	</xsl:for-each-group>
	</table>
	</body>
	</html>
</xsl:function>

<xsl:template match="/">
	<xsl:variable name="dataracelist" select="results/dataracelist/datarace"/>
	<xsl:result-document href="dataraces_by_fld.html">
		<xsl:copy-of select="chord:group_races($dataracelist, 'Fid',
			'Datarace Reports (Grouped By Field)')"/>
	</xsl:result-document>
	<xsl:result-document href="dataraces_by_obj.html">
		<xsl:copy-of select="chord:group_races($dataracelist, 'Oid',
			'Datarace Reports (Grouped By Object)')"/>
	</xsl:result-document>
</xsl:template>

</xsl:stylesheet>

