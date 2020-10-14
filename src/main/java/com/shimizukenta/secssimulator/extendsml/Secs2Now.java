package com.shimizukenta.secssimulator.extendsml;

import java.nio.charset.StandardCharsets;

import com.shimizukenta.secs.gem.Clock;
import com.shimizukenta.secs.secs2.Secs2Ascii;
import com.shimizukenta.secs.secs2.Secs2BuildException;
import com.shimizukenta.secs.secs2.Secs2ByteBuffersBuilder;
import com.shimizukenta.secs.secs2.Secs2Exception;
import com.shimizukenta.secs.sml.SmlParseException;

public class Secs2Now extends Secs2Ascii {

	private static final long serialVersionUID = 9064194857259167313L;
	
	private final int size;
	
	protected Secs2Now(int size) {
		super("");
		this.size = size;
	}
	
	protected static Secs2Now now(int size) throws SmlParseException {
		
		if ( size == 12 || size == 16 ) {
			return new Secs2Now(size);
		}
		
		throw new SmlParseException("NOW is only [12 or 16]");
	}

	
	@Override
	public int size() {
		return size;
	}
	
	private String now() {
		try {
			Clock c = Clock.now();
			if ( size == 12 ) {
				return c.toAscii12().getAscii();
			} else if ( size == 16 ) {
				return c.toAscii16().getAscii();
			}
		}
		catch ( Secs2Exception nothappen ) {
		}
		
		return "";
	}
	
	@Override
	protected void putByteBuffers(Secs2ByteBuffersBuilder buffers) throws Secs2BuildException {
		byte[] bs = now().getBytes(StandardCharsets.US_ASCII);
		putHeaderBytesToByteBuffers(buffers, bs.length);
		buffers.put(bs);
	}
	
	@Override
	public String getAscii() throws Secs2Exception {
		return now();
	}
	
	@Override
	protected String toJsonValue() {
		return "\"" + now() + "\"";
	}
	
	@Override
	public String toString() {
		return "<NOW [" + size() + "] >";
	}
	
	@Override
	protected String toStringValue() {
		return "\"" + now() + "\"";
	}


}
