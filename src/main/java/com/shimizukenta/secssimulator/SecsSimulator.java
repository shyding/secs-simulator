package com.shimizukenta.secssimulator;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import com.shimizukenta.secs.SecsCommunicator;
import com.shimizukenta.secs.SecsMessage;
import com.shimizukenta.secs.sml.SmlMessage;
import com.shimizukenta.secssimulator.macro.MacroRecipe;

public interface SecsSimulator {
	
	/**
	 * Save config to path-file.
	 * 
	 * @param path
	 * @return {@code true} if save success
	 * @throws IOException
	 */
	public boolean saveConfig(Path path) throws IOException;
	
	/**
	 * Load config from path-file.
	 * 
	 * @param path
	 * @return {@code true} if load success
	 * @throws IOException
	 */
	public boolean loadConfig(Path path) throws IOException;
	
	/**
	 * Open communicator.
	 * 
	 * @return opened-communicator
	 * @throws IOException
	 */
	public SecsCommunicator openCommunicator() throws IOException;
	
	/**
	 * Close communicator.
	 * 
	 * @throws IOException
	 */
	public void closeCommunicator() throws IOException;
	
	/**
	 * Quit Application
	 * 
	 * @throws IOException
	 */
	public void quitApplication() throws IOException;
	
	/**
	 * Communicator protocol setter.
	 * 
	 * <p>
	 * Not accept {@code null}
	 * </p>
	 * 
	 * @param protocol
	 */
	public void protocol(SecsSimulatorProtocol protocol);
	
	/**
	 * Returns communicator protocol.
	 * 
	 * @return communicator-protocol
	 */
	public SecsSimulatorProtocol protocol();
	
	/**
	 * Send SML-Message and receive Reply Message.
	 * 
	 * <p>
	 * Blocking-method<br />
	 * wait until received reply-message if exist
	 * </p>
	 * 
	 * @param sml
	 * @return Reply-Message if exist
	 * @throws SecsSimulatorException
	 * @throws InterruptedException
	 */
	public Optional<SecsMessage> send(SmlMessage sml) throws SecsSimulatorException, InterruptedException;
	
	/**
	 * Send Reply-Message.
	 * 
	 * <p>
	 * Blocking-method<br />
	 * </p>
	 * 
	 * @param primaryMsg
	 * @param replySml
	 * @return {@code Optional.empty()}
	 * @throws SecsSimulatorException
	 * @throws InterruptedException
	 */
	public Optional<SecsMessage> send(SecsMessage primaryMsg, SmlMessage replySml) throws SecsSimulatorException, InterruptedException;
	
	/**
	 * Linktest.
	 * 
	 * <p>
	 * Blocking-method<br />
	 * wait until linktest.rsp
	 * </p>
	 * 
	 * @return true if linktest success
	 * @throws InterruptedException
	 */
	public boolean linktest() throws InterruptedException;
	
	/**
	 * Add SML
	 * 
	 * @param alias of SML
	 * @param sml
	 * @return true if add success
	 */
	public boolean addSml(CharSequence alias, SmlMessage sml);
	
	/**
	 * Remove SML
	 * 
	 * @param alias of SML
	 * @return true if remove success
	 */
	public boolean removeSml(CharSequence alias);
	
	/**
	 * Start logging.
	 * 
	 * @param path of logging
	 * @return Optional has value if start-logging success
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public Optional<Path> startLogging(Path path) throws IOException, InterruptedException;
	
	/**
	 * Stop logging.
	 * 
	 * @return Optional has value if stop-logging success
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public Optional<Path> stopLogging() throws IOException, InterruptedException;
	
	/**
	 * Start Macro-recipe.
	 * 
	 * @param recipe
	 * @return Optional has value if start success
	 * @throws InterruptedException
	 */
	public Optional<MacroRecipe> startMacro(MacroRecipe recipe) throws InterruptedException;
	
	/**
	 * Stop Macro-recipe.
	 * 
	 * @return Optional has value if stop success
	 * @throws InterruptedException
	 */
	public Optional<MacroRecipe> stopMacro() throws InterruptedException;
	
}
