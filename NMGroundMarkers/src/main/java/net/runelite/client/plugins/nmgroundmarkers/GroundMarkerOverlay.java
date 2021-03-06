/*
 * Copyright (c) 2018, TheLonelyDev <https://github.com/TheLonelyDev>
 * Copyright (c) 2018, Adam <Adam@sigterm.info>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.nmgroundmarkers;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.OverlayUtil;
import plugin.nomore.nmputils.api.RenderAPI;

@Singleton
public class GroundMarkerOverlay extends Overlay
{
	private final Client client;
	private final GroundMarkerPlugin plugin;
	private final GroundMarkerConfig config;
	private final RenderAPI renderAPI;

	@Inject
	private GroundMarkerOverlay(final Client client, final GroundMarkerPlugin plugin, final GroundMarkerConfig config, RenderAPI renderAPI)
	{
		this.client = client;
		this.plugin = plugin;
		this.config = config;
		this.renderAPI = renderAPI;

		setPosition(OverlayPosition.DYNAMIC);
		setPriority(OverlayPriority.LOW);
		setLayer(OverlayLayer.ABOVE_SCENE);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		List<GroundMarkerWorldPoint> points = plugin.getPoints();
		for (GroundMarkerWorldPoint groundMarkerWorldPoint : points)
		{
			drawTile(graphics, groundMarkerWorldPoint);
		}

		return null;
	}

	private void drawTile(Graphics2D graphics, GroundMarkerWorldPoint groundMarkerWorldPoint)
	{
		WorldPoint point = groundMarkerWorldPoint.getWorldPoint();
		if (point.getPlane() != client.getPlane())
		{
			return;
		}

		LocalPoint lp = LocalPoint.fromWorld(client, point);
		if (lp == null)
		{
			return;
		}

		Polygon poly = Perspective.getCanvasTilePoly(client, lp);
		if (poly == null)
		{
			return;
		}

		Color color = config.markerColor();
		switch (groundMarkerWorldPoint.getGroundMarkerPoint().getGroup())
		{
			case 2:
				color = config.markerColor2();
				break;
			case 3:
				color = config.markerColor3();
				break;
			case 4:
				color = config.markerColor4();
				break;
			case 5:
				color = config.markerColor5();
				break;
			case 6:
				color = config.markerColor6();
				break;
			case 7:
				color = config.markerColor7();
				break;
			case 8:
				color = config.markerColor8();
				break;
			case 9:
				color = config.markerColor9();
				break;
			case 10:
				color = config.markerColor10();
				break;
			case 11:
				color = config.markerColor11();
				break;
			case 12:
				color = config.markerColor12();
		}
		if (config.boxHighlighting())
		{
			renderAPI.renderCentreBox(graphics, poly.getBounds(), color, config.boxSize());
			return;
		}
		if (config.thinMarkers())
		{
			OverlayUtil.renderPolygonThin(graphics, poly, color);
		}
		else
		{
			OverlayUtil.renderPolygon(graphics, poly, color);
		}
	}
}