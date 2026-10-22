package com.digitalpetri.enip.commands;

public abstract class Command {
   private final CommandCode commandCode;

   protected Command(CommandCode commandCode) {
      this.commandCode = commandCode;
   }

   public CommandCode getCommandCode() {
      return this.commandCode;
   }
}
