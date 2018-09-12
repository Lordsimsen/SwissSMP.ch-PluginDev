package ch.swisssmp.utils.splines;

import org.bukkit.util.Vector;

class CacheItem
{
   public CacheItem(Vector pos)
   {
      this.pos = pos;
   }

   float position;
   Vector pos;
   float travelled;
}