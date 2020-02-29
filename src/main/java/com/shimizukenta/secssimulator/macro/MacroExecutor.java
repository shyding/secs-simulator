package com.shimizukenta.secssimulator.macro;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.shimizukenta.secs.SecsCommunicatableStateChangeListener;
import com.shimizukenta.secs.SecsCommunicator;
import com.shimizukenta.secs.SecsMessage;
import com.shimizukenta.secs.sml.SmlMessage;
import com.shimizukenta.secs.sml.SmlParseException;
import com.shimizukenta.secssimulator.SecsSimulator;
import com.shimizukenta.secssimulator.SecsSimulatorException;

public class MacroExecutor {
	
	public static final long defaultSleepMilliSec = 1000L;
	
	private final SecsSimulator parent;
	private SecsMessage lastRecvMsg;
	
	public MacroExecutor(SecsSimulator parent) {
		this.parent = parent;
		this.lastRecvMsg = null;
	}
	
	public void execute(MacroRequest request) throws InterruptedException, MacroException {
		
		switch ( request.command() ) {
		case OPEN: {
			
			try {
				openCommunicator();
			}
			catch ( IOException e ) {
				throw new MacroException(e);
			}
			break;
		}
		case CLOSE: {
			
			try {
				parent.closeCommunicator();
			}
			catch ( IOException e ) {
				throw new MacroException(e);
			}
			break;
		}
		case SEND_SML: {
			
			Optional<String> alias = request.option(0);
			SmlMessage sm = alias.flatMap(parent::sml).orElse(null);
			
			if ( sm == null ) {
				
				throw new MacroException("SML-Alias not found \"" + alias.orElse("") + "\"");
				
			} else {
				
				send(sm);
			}
			break;
		}
		case SEND_DIRECT: {
			
			try {
				SmlMessage sm = parent.parseSml(request.option(0).orElse(""));
				send(sm);
			}
			catch ( SmlParseException e ) {
				throw new MacroException(e);
			}
			break;
		}
		case WAIT: {
			
			waitMessage(request.option(0).orElse(""));
			break;
		}
		case SLEEP: {
			
			try {
				long v = request.option(0)
						.map(s -> {
							return (long)(Float.parseFloat(s) * 1000.0F);
						})
						.orElse(defaultSleepMilliSec);
				
				TimeUnit.MILLISECONDS.sleep(v);
			}
			catch ( NumberFormatException e ) {
				throw new MacroException(e);
			}
			break;
		}
		default: {
			/* Nothing */
		}
		}
	}
	
	private final Object waitMsgSyncObj = new Object();
	
	public void receive(SecsMessage recvMsg) {
		synchronized ( waitMsgSyncObj ) {
			this.lastRecvMsg = recvMsg;
			waitMsgSyncObj.notifyAll();
		}
	}
	
	protected static final String GROUP_STREAM = "STREAM";
	protected static final String GROUP_FUNCTION = "FUNCTION";
	protected static final String pregMessage = "[Ss](?<" + GROUP_STREAM + ">[0-9]{1,3})[Ff](?<" + GROUP_FUNCTION + ">[0-9]{1,3})";
	
	protected static final Pattern ptnMessage = Pattern.compile("^" + pregMessage + "$");
	
	private void waitMessage(String sxfy) throws InterruptedException, MacroException {
		
		Matcher m = ptnMessage.matcher(sxfy.trim());
		
		if ( ! m.matches() ) {
			throw new MacroException("Wait preg pattern not match \"" + sxfy + "\"");
		}
		
		final int strm = Integer.parseInt(m.group(GROUP_STREAM));
		final int func = Integer.parseInt(m.group(GROUP_FUNCTION));
		
		synchronized ( waitMsgSyncObj ) {
			
			for ( ;; ) {
				
				if ( this.lastRecvMsg != null ) {
					
					try {
						if ( lastRecvMsg.getStream() == strm && lastRecvMsg.getFunction() == func ) {
							return;
						}
					}
					finally {
						this.lastRecvMsg = null;
					}
				}
				
				waitMsgSyncObj.wait();
			}
		}
	}
	
	
	private final Object commSyncObj = new Object();
	
	private void openCommunicator() throws InterruptedException, IOException {
		
		final SecsCommunicator comm = parent.openCommunicator();
		
		final SecsCommunicatableStateChangeListener lstnr = communicated -> {
			synchronized ( commSyncObj ) {
				if ( communicated ) {
					commSyncObj.notifyAll();
				}
			}
		};
		
		synchronized ( commSyncObj ) {
			try {
				comm.addSecsCommunicatableStateChangeListener(lstnr);
				commSyncObj.wait();
			}
			finally {
				comm.removeSecsCommunicatableStateChangeListener(lstnr);
			}
		}
	}
	
	private Optional<SecsMessage> send(SmlMessage sm) throws InterruptedException, MacroException {
		
		try {
			return parent.send(sm);
		}
		catch ( SecsSimulatorException e ) {
			Throwable t = e.getCause();
			if ( t == null ) {
				throw new MacroException(e);
			} else {
				throw new MacroException(t);
			}
		}
	}
}
