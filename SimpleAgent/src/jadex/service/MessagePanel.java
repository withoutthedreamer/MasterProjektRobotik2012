package jadex.service;

import jadex.agent.ConsoleAgent;
import jadex.bridge.IComponentListener;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.commons.ChangeEvent;
import jadex.commons.IChangeListener;
import jadex.commons.SGUI;
import jadex.micro.IMicroExternalAccess;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * 
 */
@SuppressWarnings("serial")
public class MessagePanel extends JPanel
{
	/** The linefeed separator. */
	public static final String lf = (String)System.getProperty("line.separator");
	
	/** The agent. */
	protected IMicroExternalAccess agent;
	
	/**
	 * 
	 */
	public MessagePanel(final IMicroExternalAccess agent)
	{
		this.agent = agent;
		
		final JTextArea ta = new JTextArea(10, 30);
		final JScrollPane main = new JScrollPane(ta);
		
		/** Register message service */
		agent.scheduleStep(new IComponentStep()
		{
			public Object execute(IInternalAccess ia)
			{
				ConsoleAgent ca = (ConsoleAgent)ia;
				ca.getMessageService().addChangeListener(new IChangeListener()
				{
					public void changeOccurred(ChangeEvent event)
					{
						String[] text = (String[])event.getValue();
						StringBuffer buf = new StringBuffer();
						buf.append("[").append(text[0]).append("]: ").append(text[1]).append(lf);
	
						ta.append(buf.toString());
						scrollToEnd(main);
					}
				});
				return null;
			}
		});

		/** Register to HelloService */
		agent.scheduleStep(new IComponentStep()
		{
			public Object execute(IInternalAccess ia)
			{
				final ConsoleAgent ca = (ConsoleAgent)ia;
				ca.getHelloService().addChangeListener(new IChangeListener()
				{
					public void changeOccurred(ChangeEvent event)
					{
						Object[] content = (Object[])event.getValue();
						StringBuffer buf = new StringBuffer();
						buf.append("[").append(content[0].toString()).append("|").append(ca.getHelloService().getClass().getName()).append("]: ").append(content[1].toString()).append(" says: ").append(content[2].toString()).append(lf);
												
						ta.append(buf.toString());
						scrollToEnd(main);
					}
				});
				return null;
			}
		});
		
		/** Register to Position update service */
		agent.scheduleStep(new IComponentStep()
		{
			public Object execute(IInternalAccess ia)
			{
				final ConsoleAgent ca = (ConsoleAgent)ia;
				ca.getSendPositionService().addChangeListener(new IChangeListener()
				{
					public void changeOccurred(ChangeEvent event)
					{
						Object[] content = (Object[])event.getValue();
						StringBuffer buf = new StringBuffer();
						buf.append("[").append(content[0].toString()).append("|").append(ca.getSendPositionService().getClass().getName()).append("]: ").append(content[1].toString()).append(" says: ").append(content[2]).append(lf);
						
						ta.append(buf.toString());
						scrollToEnd(main);
					}
				});
				return null;
			}
		});
		
		/** Register to goal reached service */
		agent.scheduleStep(new IComponentStep()
		{
			public Object execute(IInternalAccess ia)
			{
				final ConsoleAgent ca = (ConsoleAgent)ia;
				ca.getGoalReachedService().addChangeListener(new IChangeListener()
				{
					public void changeOccurred(ChangeEvent event)
					{
						Object[] content = (Object[])event.getValue();
						StringBuffer buf = new StringBuffer();
						buf.append("[").append(content[0].toString()).append("|").append(ca.getGoalReachedService().getClass().getName()).append("]: ").append(content[1].toString()).append(" says: ").append(content[2].toString()).append(lf);						

						ta.append(buf.toString());
						scrollToEnd(main);
					}
				});
				return null;
			}
		});
		
		/** Register new goal event callback */
		agent.scheduleStep(new IComponentStep()
		{
			public Object execute(IInternalAccess ia)
			{
				final ConsoleAgent ca = (ConsoleAgent)ia;
				ca.getReceiveNewGoalService().addChangeListener(new IChangeListener()
				{
					public void changeOccurred(ChangeEvent event)
					{
						Object[] content = (Object[])event.getValue();
						StringBuffer buf = new StringBuffer();
						buf.append("[").append(content[0].toString()).append("|").append(ca.getReceiveNewGoalService().getClass().getName()).append("]: ").append(content[1].toString()).append(" heading ").append(content[2].toString()).append(lf);
						
						ta.append(buf.toString());
						scrollToEnd(main);
					}
				});
				return null;
			}
		});
				
		JPanel south = new JPanel(new BorderLayout());
		final JTextField tf = new JTextField();
		JButton send = new JButton("Send");
		south.add(tf, BorderLayout.CENTER);
		south.add(send, BorderLayout.EAST);

		ActionListener al = new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				agent.scheduleStep(new IComponentStep()
				{
					public Object execute(IInternalAccess ia)
					{
						ConsoleAgent ca = (ConsoleAgent)ia;
						ca.getMessageService().tell(""+agent.getComponentIdentifier(), tf.getText());
						tf.setText("");
						return null;
					}
				});
			}
		};
		tf.addActionListener(al);
		send.addActionListener(al);
		
		this.setLayout(new BorderLayout());
		this.add(main, BorderLayout.CENTER);
		this.add(south, BorderLayout.SOUTH);
	}
	
	/**
	 *  Create a gui frame.
	 */
	public static void createGui(final IMicroExternalAccess agent)
	{
		final JFrame f = new JFrame();
		f.add(new MessagePanel(agent));
		f.pack();
		f.setLocation(SGUI.calculateMiddlePosition(f));
		f.setVisible(true);
		f.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				agent.killComponent();
			}
		});
		
		agent.scheduleStep(new IComponentStep()
		{
			public Object execute(IInternalAccess ia)
			{
				ia.addComponentListener(new IComponentListener()
				{
					public void componentTerminating(ChangeEvent ce)
					{
					}
					
					public void componentTerminated(ChangeEvent ce)
					{
						f.setVisible(false);
					}
				});
				return null;
			}
		});
	}
	
	void scrollToEnd(JScrollPane sp)
	{
		sp.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener()
		{  
			public void adjustmentValueChanged(AdjustmentEvent e) {  
				e.getAdjustable().setValue(e.getAdjustable().getMaximum());  
			}
		});  
	}
}
